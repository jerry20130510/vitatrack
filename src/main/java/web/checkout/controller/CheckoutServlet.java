package web.checkout.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import web.checkout.service.CheckoutService;
import web.checkout.vo.CheckoutResult;

@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {

    private final CheckoutService checkoutService = new CheckoutService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");

        try {
            // MVP：先用 parameter 取得 memberId（之後再換成 session 登入資訊）
            int memberId = Integer.parseInt(req.getParameter("memberId"));

            CheckoutResult result = checkoutService.checkout(memberId);

            String json =
                "{"
                + "\"orderId\":" + result.getOrderId() + ","
                + "\"totalAmount\":" + result.getTotalAmount() + ","
                + "\"status\":\"" + result.getStatus() + "\""
                + "}";

            resp.getWriter().write(json);

        } catch (Exception ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            String msg = ex.getMessage() == null ? "checkout failed" : ex.getMessage();
            msg = msg.replace("\"", "\\\""); // 簡單 escape 避免 JSON 壞掉

            String errJson =
                "{"
                + "\"error\":\"" + msg + "\""
                + "}";

            resp.getWriter().write(errJson);
        }
    }
}
