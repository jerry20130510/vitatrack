package web.product.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import web.product.dao.ProductDao;

public class ProductDaoImpl implements ProductDao {

	private DataSource ds;

	public ProductDaoImpl() {
		try {
			ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/vitatrack");
		} catch (NamingException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int getProductStock(int productId) {
		
		String sql = "SELECT stock FROM products_test WHERE product_id = ?";

		try (Connection conn = ds.getConnection(); 
			 PreparedStatement pstmt = conn.prepareStatement(sql);
		) {
		   	pstmt.setInt(1, productId);
			try (ResultSet rs = pstmt.executeQuery()){

			if (rs.next()) {
				return rs.getInt("stock");
			}
			    return 0;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
