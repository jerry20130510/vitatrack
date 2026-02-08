package web.product.service;

import web.product.vo.Product;

public interface ProductService {
    boolean create(Product product);
    boolean update(String sku, Product product);
}
