package web.product.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import web.product.service.CartItemService;
import web.product.service.impl.CartItemServiceImpl;
import web.product.vo.CartItem;
import web.product.vo.CartItemRequest;
import web.product.vo.CheckoutRequest;
import web.product.vo.CheckoutResponse;

@WebServlet("/cart-item/checkout")
public class CartItemCheckoutController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final Gson gson = new Gson();
    private final CartItemService cartItemService = new CartItemServiceImpl();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json; charset=UTF-8");

        CheckoutResponse response = new CheckoutResponse();

        try {
            BufferedReader reader = req.getReader();
            CheckoutRequest checkoutRequest = gson.fromJson(reader, CheckoutRequest.class);

            if (checkoutRequest == null || checkoutRequest.getItems() == null || checkoutRequest.getItems().isEmpty()) {
                response.setSuccess(false);
                response.setMessage("購物車資料不可為空");
                resp.getWriter().write(gson.toJson(response));
                return;
            }

            List<CartItem> cartItems = new ArrayList<>();

            for (CartItemRequest reqItem : checkoutRequest.getItems()) {
                CartItem item = new CartItem();
                item.setMemberId(26);
                item.setSku(reqItem.getSku());
                item.setQuantity(reqItem.getQuantity());

                cartItems.add(item);
            }

            boolean success = cartItemService.saveCartItems(cartItems);

            response.setSuccess(success);
            response.setMessage(success ? "cart_item 寫入成功" : "cart_item 寫入失敗");

        } catch (Exception e) {
            e.printStackTrace();
            response.setSuccess(false);
            response.setMessage("系統錯誤：" + e.getMessage());
        }

        resp.getWriter().write(gson.toJson(response));
    }
}