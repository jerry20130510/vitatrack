package web.product.service.impl;

import web.product.dao.ProductDao;
import web.product.service.ProductService;

public class ProductServiceImpl implements ProductService {

    private ProductDao productDao;

    public ProductServiceImpl(ProductDao productDao) {
        this.productDao = productDao;
    }
    
    @Override
    public int getProductStock(int productId) {
		return productDao.getProductStock(productId);
	}

}
