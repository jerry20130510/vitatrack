package web.checkout.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import web.checkout.service.CheckoutService;
import web.checkout.service.impl.CheckoutServiceImpl;
import web.checkout.vo.CheckoutResult;

@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {

    private final CheckoutService checkoutService = new CheckoutServiceImpl();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    	
        // 設定回傳格式為 JSON
        resp.setContentType("application/json; charset=UTF-8");

        try {
        	//取得請求參數
            String memberIdStr = req.getParameter("memberId");
            if (memberIdStr == null || memberIdStr.isBlank()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"memberId is required\"}");
                return;
            }

            int memberId = Integer.parseInt(memberIdStr);
            
            //呼叫 Service 執行結帳流程
            CheckoutResult result = checkoutService.checkout(memberId);

            // 成功回傳
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(
                    "{\"orderId\":" + result.getOrderId()
                            + ",\"totalAmount\":" + result.getTotalAmount()
                            + ",\"status\":\"" + escapeJson(result.getStatus()) + "\"}"
            );

        } catch (Exception ex) {

            // 在Eclipse Console 印出stack trace
            ex.printStackTrace();

            // 找 root cause
            Throwable root = ex;
            while (root.getCause() != null) root = root.getCause();

            String msg = (root.getMessage() == null) ? root.toString() : root.getMessage();

            //回傳錯誤給前端
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"" + escapeJson(msg) + "\"}");
        }
    }

    // 避免 error 裡有 " 或 \ 造成 JSON 壞掉
    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
