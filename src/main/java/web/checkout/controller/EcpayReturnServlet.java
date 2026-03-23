package web.checkout.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/checkout/ecpay/return")
public class EcpayReturnServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        String orderId = req.getParameter("orderId");
        
        if (orderId == null || orderId.isBlank()) {
            orderId = "0";
        }

        String target = req.getContextPath()
                + "/checkout/"
                + URLEncoder.encode("付款確認中.html", StandardCharsets.UTF_8.name())
                + "?orderId=" + orderId;

        resp.sendRedirect(target);
    }
    
}