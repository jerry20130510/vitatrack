package web.product.dao;

import java.util.List;
import web.product.vo.Product;

public interface ProductDao {

    Product selectBySku(String sku);

	boolean insert(Product product);

	List<Product> selectAll();

	boolean updateEditableFields(Product product);

	boolean deleteBySku(String sku);
    
}
