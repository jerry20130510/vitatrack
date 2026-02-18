package web.checkout.controller;

import java.io.IOException;
import java.sql.Connection;

import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.google.gson.Gson;

import web.checkout.service.PaymentService;
import web.checkout.service.impl.PaymentServiceImpl;
import web.checkout.vo.EcpayCheckoutPayload; // ✅ 新增：回傳 actionUrl + formParams 用

/**
 * PaymentServlet
 *
 * 功能： 對應金流功能地圖「串金流（去付款）」的 Step 1：
 *
 * 1. 後端查 orders（依 order_id） 2. 確認訂單存在 3. payment_status 必須是 PENDING 或 FAILED
 * （SUCCESS 不可再付款）
 *
 * 目前這支 Servlet 只負責： - 接收前端 orderId - 呼叫 Service 做判斷 - 回傳結果給前端
 */
@WebServlet("/api/checkout/payment")
public class PaymentServlet extends HttpServlet {

	private DataSource ds;

	// Service 層：查訂單 + 判斷是否可付款
	private final PaymentService paymentService = new PaymentServiceImpl();

	// 把 Service 回傳的 payload 轉成 JSON
	private final Gson gson = new Gson();

	// 取得資料庫 DataSource（JNDI）
	@Override
	public void init() throws ServletException {
		try {
			ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/vitatrack");
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	// 前端呼叫：POST
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		// 設定回傳格式為 JSON，編碼是 UTF-8
		resp.setContentType("application/json; charset=UTF-8");

		// 1.讀取並驗證前端傳來的 orderId
		String orderIdStr = req.getParameter("orderId");

		// (1)如果沒有傳 orderId 或內容是空
		if (orderIdStr == null || orderIdStr.isBlank()) {
			// 設定 HTTP 狀態碼為 400 Bad Request(前端送的資料不符合要求)
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			resp.getWriter().write("{\"message\":\"missing orderId\"}");
			return;
		}

		// (2)將前端發出 orderId 由字串改為int

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

		try (Connection conn = ds.getConnection()) {

			// 呼叫 Service 層：
			EcpayCheckoutPayload payload = paymentService.createEcpayCheckout(conn, orderId);

			// 若回傳 null,代表【訂單不存在】或 【payment_status = SUCCESS（不可再付款）】
			if (payload == null) {
				resp.setStatus(HttpServletResponse.SC_CONFLICT);
				resp.getWriter().write("{\"message\":\"order not found or already paid\"}");
				return;
			}

			// 若成功通過檢查(可付款)
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.getWriter().write(gson.toJson(payload));

		} catch (IllegalArgumentException e) {
			// 伺服器錯誤(例如資料庫錯誤)：訂單不存在 / 參數錯誤
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			resp.getWriter().write("{\"message\":\"" + escapeJson(e.getMessage()) + "\"}");

		} catch (IllegalStateException e) {
			// 伺服器錯誤(例如資料庫錯誤)：狀態不允許付款（例如 SUCCESS）
			resp.setStatus(HttpServletResponse.SC_CONFLICT);
			resp.getWriter().write("{\"message\":\"" + escapeJson(e.getMessage()) + "\"}");

		} catch (Exception e) {

			// 伺服器錯誤(例如資料庫錯誤)
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
