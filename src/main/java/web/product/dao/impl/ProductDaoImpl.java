package web.product.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.hibernate.Session;

import web.product.dao.ProductDao;
import web.product.vo.Product;

public class ProductDaoImpl implements ProductDao {

	private DataSource ds;

	public ProductDaoImpl() {
		try {
			ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/vitatrack");
		} catch (NamingException e) {
			e.printStackTrace();
		}

	}

	@Override
	public boolean insert(Product product) {
		Tra
		Session session = Session.
		
		String sql = "INSERT INTO product "
				+ "(sku, category_id, product_name, size, price, stock_quantity, status, short_description, description, created_by_admin_id) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try (Connection conn = ds.getConnection(); 
				PreparedStatement ps = conn.prepareStatement(sql);
				
		){
			ps.setString(1, product.getSku());
			ps.setLong(2, product.getCategoryId());
			ps.setString(3, product.getProductName());
			ps.setString(4, product.getSize());
			ps.setInt(5, product.getPrice());
			ps.setInt(6, product.getStockQuantity());
			ps.setInt(7, product.getStatus());
			ps.setString(8, product.getShortDescription());
			ps.setString(9, product.getDescription());
			ps.setLong(10, product.getCreatedByAdminId());

			int affected = ps.executeUpdate();
			return affected == 1;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean updateBySku(String sku, Product product) {
	    String sql = "UPDATE product SET "
	            + "category_id = ?, "
	            + "product_name = ?, "
	            + "size = ?, "
	            + "price = ?, "
	            + "stock_quantity = ?, "
	            + "status = ?, "
	            + "short_description = ?, "
	            + "description = ?, "
	            + "updated_by_admin_id = ? "
	            + "WHERE sku = ?";		
	    
	    try (Connection conn = ds.getConnection();
	            PreparedStatement ps = conn.prepareStatement(sql)) {
		           ps.setInt(1, product.getCategoryId());
		           ps.setString(2, product.getProductName());
		           ps.setString(3, product.getSize());
		           ps.setInt(4, product.getPrice());
		           ps.setInt(5, product.getStockQuantity());
		           ps.setLong(6, product.getStatus());
		           ps.setString(7, product.getShortDescription());
		           ps.setString(8, product.getDescription());
		           ps.setInt(9, product.getUpdatedByAdminId());
		           ps.setString(10, sku);
		           
		           return ps.executeUpdate() == 1;
		           
	    }catch (SQLException e) {
			e.printStackTrace();
		}
	    return false;
	}
}
