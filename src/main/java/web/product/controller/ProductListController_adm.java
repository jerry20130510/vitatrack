package web.product.controller;


import java.util.List;


import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import web.product.service.ProductService;

import web.product.vo.Product;

@WebServlet("/product-list")
public class ProductListController_adm extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@Autowired
	private ProductService productService;

	@GetMapping("/product-list")
	public ResponseEntity<List<Product>> getProductList() {
		List<Product> list = productService.selectAll();

		return ResponseEntity.ok(list);

	}

}
