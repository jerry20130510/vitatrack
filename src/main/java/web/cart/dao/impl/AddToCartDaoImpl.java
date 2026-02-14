package web.cart.dao.impl;

import java.sql.Connection;

import java.sql.PreparedStatement;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import web.cart.dao.AddToCartDao;


public class AddToCartDaoImpl implements AddToCartDao {
	
	  private DataSource ds;

	    public AddToCartDaoImpl() {
	        try {
	            ds = (DataSource) new InitialContext()
	                    .lookup("java:comp/env/jdbc/vitatrack");
	        } catch (NamingException e) {
	            throw new RuntimeException(e);
	        }
	    }

	    @Override
	    public void addToCart(int productId, int quantity) {

	        String sql = "INSERT INTO carts_item_test (product_id, qty) VALUES (?, ?)";

	        try (Connection conn = ds.getConnection();
	             PreparedStatement ps = conn.prepareStatement(sql)) {
	        	
	        	ps.setInt(1, productId);
	            ps.setInt(2, quantity);
	            ps.executeUpdate();

	        } catch (Exception e) {
	            throw new RuntimeException(e);
	        }
	    }
}
