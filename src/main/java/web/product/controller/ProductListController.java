package web.product.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import web.product.service.ProductService;
import web.product.service.impl.ProductServiceImpl;
import web.product.vo.Product;

@WebServlet("/product-list")
public class ProductListController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ProductService productService;
	
	@Override
	public void init() throws ServletException {
		try {
			productService = new ProductServiceImpl();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			resp.setContentType("application/json;charset=utf-8");
		try {
	            List<Product> list = productService.selectAll();

	            Gson gson = new Gson();
	            resp.getWriter().write(gson.toJson(list));

	        } catch (Exception e) {
	            e.printStackTrace();
	            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	            resp.getWriter().write("{\"success\": false}");
	        }
	 }	
}
