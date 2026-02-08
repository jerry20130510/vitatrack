package web.product.service.impl;

import web.product.dao.ProductDao;
import web.product.dao.impl.ProductDaoImpl;
import web.product.service.ProductService;
import web.product.vo.Product;

public class ProductServiceImpl implements ProductService {
    final ProductDao productDao = new ProductDaoImpl();

    @Override
	public boolean create(Product product) {
		if (product == null) return false;

        if (product.getProductName() == null || product.getProductName().trim().isEmpty()) {
            return false;
        }
        if (product.getPrice() <= 0) return false;
        if (product.getStockQuantity() < 0) return false;

        if (product.getStatus() != 0 && product.getStatus() != 1) {
            product.setStatus(1);
        }

        product.setCreatedByAdminId(1L);
        return productDao.insert(product);
    }

	@Override
	public boolean update(String sku, Product product) {
	    if (sku == null || sku.trim().isEmpty()) return false;
	    if (product == null) return false;
	    if (product.getProductName() == null || product.getProductName().trim().isEmpty()) return false;
	    if (product.getStockQuantity() < 0) return false;

	    product.setUpdatedByAdminId(1);

	    return productDao.updateBySku(sku, product);
	}
	

}
