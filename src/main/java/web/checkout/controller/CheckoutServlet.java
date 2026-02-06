package web.checkout.controller; // ← 依你實際位置調整

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
    	
        // 統一回 JSON，避免 DevTools Response 看到空白
        resp.setContentType("application/json; charset=UTF-8");

        try {
            String memberIdStr = req.getParameter("memberId");
            if (memberIdStr == null || memberIdStr.isBlank()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"memberId is required\"}");
                return;
            }

            int memberId = Integer.parseInt(memberIdStr);

            CheckoutResult result = checkoutService.checkout(memberId);

            // 成功回傳
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(
                    "{\"orderId\":" + result.getOrderId()
                            + ",\"totalAmount\":" + result.getTotalAmount()
                            + ",\"status\":\"" + escapeJson(result.getStatus()) + "\"}"
            );

        } catch (Exception ex) {

            // ✅ 1) 一定要印，不然 Eclipse Console 永遠看不到真正原因
            ex.printStackTrace();

            // ✅ 2) 找 root cause（通常 SQLException 在最底層）
            Throwable root = ex;
            while (root.getCause() != null) root = root.getCause();

            String msg = (root.getMessage() == null) ? root.toString() : root.getMessage();

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"" + escapeJson(msg) + "\"}");
        }
    }

    // 簡單 JSON escape，避免 error 裡有 " 或 \ 造成 JSON 壞掉
    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
