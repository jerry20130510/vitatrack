package cart.dao.impl;

import java.sql.Connection;

import java.sql.PreparedStatement;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import cart.dao.AddToCartDao;
import vo.CartItem;


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
	    public void insertCartItem(CartItem item) {

	        String sql = "INSERT INTO carts_item_test (product_id, qty) VALUES (?, ?)";

	        try (Connection conn = ds.getConnection();
	             PreparedStatement ps = conn.prepareStatement(sql)) {

	            ps.setInt(1, item.getProductId());
	            ps.setInt(2, item.getQty());
	            ps.executeUpdate();

	        } catch (Exception e) {
	            throw new RuntimeException(e);
	        }
	    }
}
