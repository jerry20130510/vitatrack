package web.product.dao;

import java.util.List;
import web.product.vo.Product;

public interface ProductDao {

	boolean insert(Product product);

	List<Product> selectAll();

	boolean updateEditableFields(Product product);

	boolean deleteBySku(String sku);
    
}
