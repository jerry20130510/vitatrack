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


@WebServlet("/product-add")
public class ProductAddController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ProductService productService;
	
	@Override
	public void init() throws ServletException {
		productService = new ProductServiceImpl();
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        Product product = gson.fromJson(req.getReader(), Product.class);

        boolean success = productService.add(product);

        resp.getWriter().write(gson.toJson(success));
	}

}
