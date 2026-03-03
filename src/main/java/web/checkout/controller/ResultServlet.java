package web.checkout.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import web.checkout.service.ResultService;
import web.checkout.service.impl.ResultServiceImpl;
import web.checkout.vo.ResultDTO;

@WebServlet("/api/checkout/result")
public class ResultServlet extends HttpServlet {

	private final ResultService resultService = new ResultServiceImpl();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		resp.setContentType("application/json; charset=UTF-8");
		resp.setCharacterEncoding("UTF-8");

		// 1.取得orderId
		String orderIdStr = req.getParameter("orderId");
		if (orderIdStr == null || orderIdStr.isBlank()) {
			resp.setStatus(400);
			resp.getWriter().write("{\"message\":\"Missing required query param: orderId\"}");
			return;
		}

		// 2.把 orderIdStr 轉成 int
		int orderId;

		try {
			orderId = Integer.parseInt(orderIdStr.trim());
		} catch (NumberFormatException e) {
			resp.setStatus(400);
			resp.getWriter().write("{\"message\":\"orderId must be an integer\"}");
			return;
		}

		// 3.呼叫 service 取得此筆訂單相關資料
		ResultDTO row = resultService.getOrder(orderId);

		if (row == null) {
			resp.setStatus(404);
			resp.getWriter().write("{\"message\":\"Order not found\"}");
			return;
		}
		
		// 4.回傳 JSON 給前端
		resp.setStatus(200);
		com.google.gson.Gson gson = new com.google.gson.Gson();
		resp.getWriter().write(gson.toJson(row));
	}
}
