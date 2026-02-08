package web.checkout.dao.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

//import javax.naming.InitialContext;
//import javax.naming.NamingException;
//import javax.sql.DataSource;

import web.checkout.dao.OrderItemDao;
import web.checkout.vo.CartRow;

public class OrderItemDaoImpl implements OrderItemDao {
//	private DataSource ds;
//
//	public OrderItemDaoImpl() {
//		try {
//			ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/vitatrack");
//		} catch (NamingException e) {
//			e.printStackTrace();
//		}
//	}
    @Override
    public int[] batchInsertFromCart(Connection conn, int orderId, List<CartRow> cartRows)
            throws SQLException {

        // 對齊order_item 欄位：
        // item_id (auto_increment) 不用塞
        // created_at (DEFAULT CURRENT_TIMESTAMP) 不用塞
        String sql =
            "INSERT INTO order_item " +
            "(order_id, sku, product_name, unit_price, quantity, subtotal) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            for (CartRow row : cartRows) {

            	//取出單價
                BigDecimal unitPrice = row.getUnitPrice();
                //取出數量
                int qty = row.getQuantity();
                //計算小計
                BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(qty));

                ps.setInt(1, orderId);
                ps.setString(2, row.getSku());
                ps.setString(3, row.getProductName()); 
                ps.setBigDecimal(4, unitPrice);
                ps.setInt(5, qty);
                ps.setBigDecimal(6, subtotal);

                ps.addBatch();
            }
            
            //將結果insert進資料庫
            return ps.executeBatch();
        }
    }
}
