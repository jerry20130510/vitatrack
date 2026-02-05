package cart.dao.impl;

import java.sql.Connection;

import java.sql.PreparedStatement;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import cart.dao.CartDao;

public class CartDaoImpl implements CartDao {

    private DataSource ds;

    public CartDaoImpl() {
        try {
            ds = (DataSource) new InitialContext()
                    .lookup("java:comp/env/jdbc/vitatrack");
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void addItem(int cart_item_id, int productId, int qty) {
        String sql =
            "INSERT INTO carts_item_test (cart_item_id, product_id, qty) VALUES (?, ?, ?)";

        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, productId);
            ps.setInt(3, quantity);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
