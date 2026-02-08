package web.product.dao;

import web.product.vo.Product;

public interface ProductDao {
	
	boolean insert (Product product);
	boolean updateBySku(String sku, Product product);

}
