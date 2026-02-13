package web.cart.controller;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import web.cart.service.AddToCartService;
import web.cart.service.impl.AddToCartServiceImpl;
import web.cart.vo.AddToCartItem;

@WebServlet("/api/cart/add")
public class AddToCartController extends HttpServlet {

    private AddToCartService addToCartService = new AddToCartServiceImpl();
    

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("application/json;charset=UTF-8");

        Gson gson = new Gson();

        AddToCartItem addToCartItem = gson.fromJson(req.getReader(), AddToCartItem.class);

        addToCartService.addToCart(addToCartItem.getProductId(), addToCartItem.getQty());

        JsonObject result = new JsonObject();
        result.addProperty("message", "加入購物車成功");

        resp.getWriter().write(gson.toJson(result));
    }
}

