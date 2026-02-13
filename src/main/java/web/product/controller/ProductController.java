package web.product.controller;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import web.product.dao.ProductDao;
import web.product.dao.impl.ProductDaoImpl;
import web.product.service.ProductService;
import web.product.service.impl.ProductServiceImpl;
import web.product.vo.StockItem;

@WebServlet("/api/product/stock")
public class ProductController extends HttpServlet {

	private final ProductService productService = new ProductServiceImpl(new ProductDaoImpl());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		resp.setContentType("application/json;charset=UTF-8");

		Gson gson = new Gson();

		String pid = req.getParameter("productId");

		int productId = Integer.parseInt(pid);

		int stock = productService.getProductStock(productId);

		if (stock == -1) {
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			JsonObject error = new JsonObject();
			error.addProperty("error", "查無商品庫存");
			resp.getWriter().write(gson.toJson(error));

		} else {
			JsonObject result = new JsonObject();
			result.addProperty("stock", stock);
			resp.getWriter().write(gson.toJson(result));
		};
	}

}
