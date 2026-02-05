package product.controller;
import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import product.dao.ProductDao;
import product.dao.impl.ProductDaoImpl;
import product.service.ProductService;
import product.service.impl.ProductServiceImpl;

@WebServlet("/api/product/stock")
public class ProductController extends HttpServlet {

    private ProductService productService;

    @Override
    public void init() {
        DataSource ds = (DataSource) getServletContext().getAttribute("ds");
        ProductDao productDao = new ProductDaoImpl();
        productService = new ProductServiceImpl(productDao);
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("application/json;charset=UTF-8");

        String pid = req.getParameter("productId");
        if (pid == null || pid.isBlank()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"missing productId\"}");
            return;
        }

        int productId;
        try {
            productId = Integer.parseInt(pid);
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"invalid productId\"}");
            return;
        }

        int stock = productService.getProductStock(productId);

        // 視你的需求：0 是真的沒庫存，還是查不到商品？
        // 這裡我不幫你亂判斷，只照你 DAO 的回傳設計
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(
            "{\"productId\":" + productId + ",\"stock\":" + stock + "}"
        );
    }
}

