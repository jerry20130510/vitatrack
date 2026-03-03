package web.product.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import web.product.service.ProductService;
import web.product.service.impl.ProductServiceImpl;
import web.product.vo.Product;

@WebServlet("/product-detail")
public class ProductDetailControlle extends HttpServlet{
	private static final long serialVersionUID = 1L;

    private ProductService productService;

    @Override
    public void init() throws ServletException {
        productService = new ProductServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json;charset=utf-8");

        String sku = req.getParameter("sku");
        if (sku == null || sku.trim().isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{}");
            return;
        }

        try {
            Product p = productService.selectBySku(sku);
            if (p == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{}");
                return;
            }

            Gson gson = new Gson();
            resp.getWriter().write(gson.toJson(p));
            
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{}");
        }
    }
}
