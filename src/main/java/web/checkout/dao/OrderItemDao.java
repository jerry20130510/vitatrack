package web.checkout.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import web.checkout.vo.CartRow;

public class OrderItemDao {

    private static final String INSERT_SQL =
        "INSERT INTO order_item " +
        "(order_id, sku, product_name, unit_price, quantity, subtotal) " +
        "VALUES (?, ?, ?, ?, ?, ?)";

    /**
     * 將購物車多筆 CartRow 寫入 order_item（同一筆訂單 orderId）
     * 使用 batch insert，需由外部傳入同一個 Connection（配合 transaction）
     */
    public int[] insertBatch(Connection conn, int orderId, List<CartRow> cartRows) throws SQLException {

        try (PreparedStatement ps = conn.prepareStatement(INSERT_SQL)) {

            for (CartRow row : cartRows) {
                ps.setInt(1, orderId);
                ps.setString(2, row.getSku());
                ps.setString(3, row.getProductName());

                // CartRow.unitPrice 目前是 int（你之前寫的是 int）
                // 這裡轉成 BigDecimal 對應 MySQL decimal
                BigDecimal unitPrice = BigDecimal.valueOf(row.getUnitPrice());
                BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(row.getQuantity()));

                ps.setBigDecimal(4, unitPrice);
                ps.setInt(5, row.getQuantity());
                ps.setBigDecimal(6, subtotal);

                ps.addBatch();
            }

            return ps.executeBatch();
        }
    }
}
