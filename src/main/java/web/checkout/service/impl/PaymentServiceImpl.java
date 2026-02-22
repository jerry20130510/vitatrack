package web.checkout.service.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import web.checkout.dao.OrderDao;
import web.checkout.dao.OrderItemDao;
import web.checkout.dao.impl.OrderDaoImpl;
import web.checkout.dao.impl.OrderItemDaoImpl;
import web.checkout.service.PaymentService;
import web.checkout.vo.EcpayCheckoutPayload;
import web.checkout.vo.OrderPaymentInfo;

public class PaymentServiceImpl implements PaymentService {

	private final OrderDao orderDao = new OrderDaoImpl();
	private final OrderItemDao orderItemDao = new OrderItemDaoImpl();

	// ===== 測試環境綠界付款網址 =====
	private static final String ECPAY_ACTION_URL_STAGE = "https://payment-stage.ecpay.com.tw/Cashier/AioCheckOut/V5";

	// ===== 建議改成設定檔，現在先寫死讓你跑通 =====
	private static final String MERCHANT_ID = "3002607";
	private static final String RETURN_URL = "https://leaseless-eventfully-sharyn.ngrok-free.dev/vitatrack/checkout/ecpay/callback";
	private static final String ORDER_RESULT_URL = "https://leaseless-eventfully-sharyn.ngrok-free.dev/vitatrack/checkout/付款成功.html";
	private static final String TRADE_DESC = "Vitatrack訂單";
	private static final String CHOOSE_PAYMENT = "ALL";
	private static final String PAYMENT_TYPE = "aio";
	private static final String ENCRYPT_TYPE = "1";
	private static final String HASH_KEY = "pwFHCqoQZGmho4w6";
	private static final String HASH_IV = "EkRm7IFT261dpevs";

	// 確認是否有訂單
	@Override
	public OrderPaymentInfo validateOrderCanPay(Connection conn, int orderId) throws SQLException {

		// 1.查訂單
		OrderPaymentInfo info = orderDao.selectPaymentInfoByOrderId(conn, orderId);

		// (1).若訂單不存在
		if (info == null) {
			return null;
		}

		// (2).payment_status = SUCCESS -> 不可再付款
		if ("SUCCESS".equalsIgnoreCase(info.getPaymentStatus())) {
			// 直接回傳 null，表示「不可付款」
			return null;
		}

		// (3).PENDING / FAILED → 允許付款(放行)
		// 產生交易編號(唯一值)
		String transactionId = "TXN" + System.currentTimeMillis();

		// 更新 orders.transaction_id
		orderDao.updateTransactionId(conn, orderId, transactionId);

		// 把交易號放回 VO
		info.setTransactionId(transactionId);

		// 從 order_items 撈 product_name
		List<String> names = orderItemDao.selectProductNamesByOrderId(conn, orderId);

		// 沒有商品明細
		if (names == null || names.isEmpty()) {
			return null;
		}

		// 組itemName
		String itemName = String.join("#", names);
		info.setItemName(itemName);

		return info;
	}

	// 組綠界所需 payload (actionUrl + formParams)
	// 先呼叫 validateOrderCanPay()
	// CheckMacValue 先空字串，之後再補
	@Override
	public EcpayCheckoutPayload createEcpayCheckout(Connection conn, int orderId) throws SQLException {

		// 先呼叫 validateOrderCanPay()
		OrderPaymentInfo info = validateOrderCanPay(conn, orderId);
		if (info == null) {
			return null;
		}

		// 綠界要求格式 yyyy/MM/dd HH:mm:ss
		String merchantTradeDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());

		// 用 LinkedHashMap 保持排列順序（盡量比照API文件）
		Map<String, String> formParams = new LinkedHashMap<>();

		formParams.put("MerchantID", MERCHANT_ID);
		formParams.put("MerchantTradeNo", info.getTransactionId());
		formParams.put("MerchantTradeDate", merchantTradeDate);
		formParams.put("PaymentType", PAYMENT_TYPE);
		formParams.put("TotalAmount", String.valueOf(info.getTotalAmount()));
		formParams.put("TradeDesc", TRADE_DESC);
		formParams.put("ItemName", info.getItemName());
		formParams.put("ReturnURL", RETURN_URL);
		formParams.put("OrderResultURL", ORDER_RESULT_URL);
		formParams.put("ChoosePayment", CHOOSE_PAYMENT);
		formParams.put("EncryptType", ENCRYPT_TYPE);
		formParams.put("CheckMacValue", genCheckMacValueSha256(formParams));

		return new EcpayCheckoutPayload(ECPAY_ACTION_URL_STAGE, formParams);
	}

	// 計算 CheckMacValue
	private String genCheckMacValueSha256(Map<String, String> params) {
		// 排序(A->Z)
		Map<String, String> sorted = new java.util.TreeMap<>();
		sorted.putAll(params);
		// 且不含 CheckMacValue
		sorted.remove("CheckMacValue");

		// 組成字串
		String sb = "";
		for (Map.Entry<String, String> e : sorted.entrySet()) {
			if (!sb.equals("")) {
				sb = sb + "&";
			}
			String key = e.getKey();
			String value = e.getValue();
			if (value == null) {
				value = "";
			}
			// 拼接成 key=value
			sb = sb + key + "=" + value;
		}

		// 前後加 HashKey / HashIV
		String raw = "HashKey=" + HASH_KEY + "&" + sb + "&HashIV=" + HASH_IV;

		// 4) URL Encode（依綠界規則）
		String encoded = ecpayUrlEncode(raw);

		// 5) SHA256 -> 大寫
		return sha256(encoded).toUpperCase();
	}

	private String ecpayUrlEncode(String raw) {
		try {
			// 綠界文件範例顯示：URL encode 後空白應為 '+'（RFC1866），不要自行換成 %20
			String encoded = java.net.URLEncoder.encode(raw, java.nio.charset.StandardCharsets.UTF_8.name());

			// 綠界規則常見替換（維持相容性）
			encoded = encoded.replace("%2D", "-").replace("%2d", "-");
			encoded = encoded.replace("%5F", "_").replace("%5f", "_");
			encoded = encoded.replace("%2E", ".").replace("%2e", ".");
			encoded = encoded.replace("%21", "!");
			encoded = encoded.replace("%28", "(").replace("%29", ")");
			encoded = encoded.replace("%2A", "*").replace("%2a", "*");
			encoded = encoded.replace("%7E", "~").replace("%7e", "~");

			// 轉小寫
			return encoded.toLowerCase();
		} catch (Exception e) {
			throw new RuntimeException("URL Encode failed", e);
		}
	}

	private String sha256(String s) {
		try {
			java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
			byte[] bytes = md.digest(s.getBytes(java.nio.charset.StandardCharsets.UTF_8));
			StringBuilder sb = new StringBuilder(bytes.length * 2);
			for (byte b : bytes) {
				sb.append(String.format("%02x", b));
			}
			return sb.toString();
		} catch (Exception e) {
			throw new RuntimeException("SHA-256 failed", e);
		}
	}

}
