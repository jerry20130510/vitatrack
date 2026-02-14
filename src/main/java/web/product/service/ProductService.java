package web.product.service;

import java.util.List;

import web.product.vo.Product;

public interface ProductService {
	boolean add(Product product);

	List<Product> selectAll();
}
