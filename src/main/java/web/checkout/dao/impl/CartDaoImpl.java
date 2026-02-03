package web.checkout.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import core.util.DbUtil;
import web.checkout.dao.CartDao;
import web.checkout.vo.CartRow;

public class CartDaoImpl implements CartDao {

    /**
     * 一般查詢（自己開 Connection，用在非 transaction 場景）
     */
    @Override
    public List<CartRow> findOpenCartByMemberId(int memberId) {

        String sql =
            "SELECT ci.cart_item_id, p.sku, p.product_name, p.price, ci.quantity " +
            "FROM cart_item ci " +
            "JOIN product p ON ci.sku = p.sku " +
            "WHERE ci.member_id = ? AND ci.order_id IS NULL";

        List<CartRow> result = new ArrayList<>();

        try (Connection conn = DbUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, memberId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CartRow row = new CartRow(
                        rs.getInt("cart_item_id"),
                        rs.getString("sku"),
                        rs.getString("product_name"),
                        rs.getBigDecimal("price"),   // ✅ BigDecimal
                        rs.getInt("quantity")
                    );
                    result.add(row);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to query cart items", e);
        }

        return result;
    }

    /**
     * Transaction 版查詢（使用外部傳入的 Connection）
     */
    @Override
    public List<CartRow> findOpenCartByMemberId(Connection conn, int memberId) throws SQLException {

        String sql =
            "SELECT ci.cart_item_id, p.sku, p.product_name, p.price, ci.quantity " +
            "FROM cart_item ci " +
            "JOIN product p ON ci.sku = p.sku " +
            "WHERE ci.member_id = ? AND ci.order_id IS NULL";

        List<CartRow> list = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, memberId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CartRow row = new CartRow(
                        rs.getInt("cart_item_id"),
                        rs.getString("sku"),
                        rs.getString("product_name"),
                        rs.getBigDecimal("price"),   // ✅ BigDecimal
                        rs.getInt("quantity")
                    );
                    list.add(row);
                }
            }
        }

        return list;
    }

    /**
     * 將購物車項目綁定到訂單（UPDATE cart_item.order_id）
     */
    @Override
    public int[] attachCartItemsToOrder(Connection conn, int orderId, List<CartRow> cartRows) throws SQLException {

        String sql = "UPDATE cart_item SET order_id = ? WHERE cart_item_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            for (CartRow row : cartRows) {
                ps.setInt(1, orderId);
                ps.setInt(2, row.getCartItemId());
                ps.addBatch();
            }

            return ps.executeBatch();
        }
    }
}
