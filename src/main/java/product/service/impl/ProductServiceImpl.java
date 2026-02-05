package product.service.impl;

import product.dao.ProductDao;
import product.service.ProductService;

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
