package web.checkout.controller;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import web.checkout.service.PaymentService;
import web.checkout.service.impl.PaymentServiceImpl;
import web.checkout.vo.EcpayCheckoutPayload; // ✅ 新增：回傳 actionUrl + formParams 用

// 接收前端 orderId -> 呼叫 Service 做判斷 -> 回傳結果給前端
@WebServlet("/api/checkout/payment")
public class PaymentServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	// 呼叫 paymentService
	private final PaymentService paymentService = new PaymentServiceImpl();
	// 把 Service 回傳的 payload 轉成 JSON
	private final Gson gson = new Gson();

	// 前端呼叫：POST
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		// 1.設定回傳格式為 JSON，編碼是 UTF-8
		resp.setContentType("application/json; charset=UTF-8");
		// 1.1 讀取並驗證前端傳來的 orderId
		String orderIdStr = req.getParameter("orderId");
		// 1.2 如果沒有傳 orderId 或內容是空
		if (orderIdStr == null || orderIdStr.isBlank()) {
			// 設定 HTTP 狀態碼為 400 Bad Request(前端送的資料不符合要求)
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			resp.getWriter().write("{\"message\":\"missing orderId\"}");
			return;
		}

		// 1.3 將前端發出 orderId 由字串改為int
		// 宣告區域變數
		int orderId;
		// 由字串改為int
		try {
			orderId = Integer.parseInt(orderIdStr);
			// 轉製失敗
		} catch (NumberFormatException e) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			resp.getWriter().write("{\"message\":\"orderId must be a number\"}");
			return;
		}

		// 2.查訂單 + 檢查 payment_status
		try {
			// 2.1 呼叫 Service
			EcpayCheckoutPayload payload = paymentService.createEcpayCheckout(orderId);
			// 2.2 若回傳 null,代表【訂單不存在】或 【payment_status = SUCCESS】
			if (payload == null) {
				// 409 Conflict(請求衝突)
				resp.setStatus(HttpServletResponse.SC_CONFLICT);
				resp.getWriter().write("{\"message\":\"order not found or already paid\"}");
				return;
			}
			// 2.3若成功通過檢查(可付款)
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.getWriter().write(gson.toJson(payload));
		// 請求參數錯誤
		} catch (IllegalArgumentException e) {
			// 400(前端傳來的資料錯誤)
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			resp.getWriter().write("{\"message\":\"" + escapeJson(e.getMessage()) + "\"}");

		} catch (Exception e) {

			// 500後端問題
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			resp.getWriter().write("{\"message\":\"server error\"}");
		}
	}

	// 避免 message 內有雙引號導致 JSON 壞掉
	private String escapeJson(String s) {
		if (s == null)
			return "";
		return s.replace("\\", "\\\\").replace("\"", "\\\"");
	}
}
