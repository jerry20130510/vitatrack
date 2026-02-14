package web.product.service.impl;


import java.util.List;

import web.product.dao.ProductDao;
import web.product.dao.impl.ProductDaoImpl;
import web.product.service.ProductService;
import web.product.vo.Product;


public class ProductServiceImpl implements ProductService {
<<<<<<< HEAD
    final ProductDao productDao = new ProductDaoImpl();

    @Override
	public boolean create(Product product) {
		if (product == null) {
			return false;
		}
        if (product.getProductName() == null || product.getProductName().trim().isEmpty()) {
            return false;
        }
        if (product.getPrice() <= 0) {
        	return false;
        }
        if (product.getStockQuantity() < 0) {
        	return false;
    	}
        if (product.getStatus() != 0 && product.getStatus() != 1) {
            product.setStatus(1);
        }
        product.setCreatedByAdminId(1L);
        return productDao.insert(product);
    }

	@Override
	public boolean update(String sku, Product product) {
	    if (sku == null || sku.trim().isEmpty()) {
	    	return false;
	    }
	    if (product == null) {
	    	return false;
	    }
	    if (product.getProductName() == null || product.getProductName().trim().isEmpty()) {
	    	return false;
	    }
	    if (product.getStockQuantity() < 0) {
	    	return false;
	    }
	    product.setUpdatedByAdminId(1);

	    return productDao.updateBySku(sku, product);
=======
	private ProductDao productDao;
	
	public ProductServiceImpl(){
		productDao = new ProductDaoImpl();
>>>>>>> main
	}
	
	@Override
	public boolean add(Product product) {
	    if (product == null) {
	        return false;
	    }
	    // 商品名稱
	    if (product.getProductName() == null){
	        return false;
	    }
	    // 價格 > 0
	    if (product.getPrice() == null || product.getPrice().compareTo(java.math.BigDecimal.ZERO) <= 0) {
	        return false;
	    }
	    // 顆數 > 0
	    if (product.getSize() == null || product.getSize().trim().isEmpty()) {
	        return false;
	    }
	    // 商品數量 > 0
	    if (product.getStockQuantity() <= 0) {
	        return false;
	    }
	    // 商品描述
	    if (product.getDescription() == null || product.getDescription().trim().isEmpty()) {
	        return false;
	    }
	    // 商品簡述
	    if (product.getShortDescription() == null || product.getShortDescription().trim().isEmpty()) {
	        return false;
	    }
	    // 商品狀態是否為上架
	    if (product.getStatus() == null || product.getShortDescription().trim().isEmpty()) {
	        return false;
	    }
	    
	    product.setSku(generateSku());
	    product.setCreatedByAdminId(1L);
	    product.setCategoryId(1);

	    
	    // 驗證通過才寫入資料庫
	    return productDao.insert(product);
	}
	//自動取sku編號
	private String generateSku() {
	    String datePart = new java.text.SimpleDateFormat("yyyyMMdd")
	            .format(new java.util.Date());

	    String randomPart = String.valueOf((int)(Math.random() * 9000) + 1000);

	    return datePart + randomPart;
	}

	@Override
	public List<Product> selectAll() {
		return productDao.selectAll();
	}

}
