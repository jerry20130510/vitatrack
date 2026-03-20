package web.checkout.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import web.checkout.service.CheckoutService;
import web.checkout.vo.CartRow;
import web.checkout.vo.CheckoutResult;

@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

    private CheckoutService checkoutService;

    @Override
    public void init() {
        ApplicationContext applicationContext =
            WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        checkoutService = applicationContext.getBean(CheckoutService.class);
    }

    
 // 進入 checkout 頁面時，先查購物車資料
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json; charset=UTF-8");

        try {
            String memberIdStr = req.getParameter("memberId");
            if (memberIdStr == null || memberIdStr.isBlank()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"memberId is required\"}");
                return;
            }

            int memberId = Integer.parseInt(memberIdStr);

            List<CartRow> cartList = checkoutService.getCheckoutCart(memberId);

            StringBuilder json = new StringBuilder();
            json.append("[");

            for (int i = 0; i < cartList.size(); i++) {
                CartRow item = cartList.get(i);

                json.append("{")
                    .append("\"cartItemId\":").append(item.getCartItemId()).append(",")
                    .append("\"sku\":\"").append(escapeJson(item.getSku())).append("\",")
                    .append("\"productName\":\"").append(escapeJson(item.getProductName())).append("\",")
                    .append("\"price\":").append(item.getUnitPrice()).append(",")
                    .append("\"quantity\":").append(item.getQuantity())
                    .append("}");

                if (i < cartList.size() - 1) {
                    json.append(",");
                }
            }

            json.append("]");

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(json.toString());

        } catch (Exception ex) {
            ex.printStackTrace();

            Throwable root = ex;
            while (root.getCause() != null) root = root.getCause();

            String msg = (root.getMessage() == null) ? root.toString() : root.getMessage();

            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"" + escapeJson(msg) + "\"}");
        }
    }
    
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
