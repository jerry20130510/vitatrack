package web.checkout.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import web.checkout.dao.OrderDao;
import web.checkout.vo.OrderPaymentInfo;

public class OrderDaoImpl implements OrderDao {

	@Override
	public int insertOrder(Connection conn, int memberId, int totalAmount, String paymentMethod,
			String paymentStatus, int amount, String transactionId) throws SQLException {

		String sql = "INSERT INTO orders "
				+ "(member_id, total_amount, payment_method, payment_status, amount, transaction_id) "
				+ "VALUES (?, ?, ?, ?, ?, ?)";

		try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			ps.setInt(1, memberId);
			ps.setInt(2, totalAmount); // int
			ps.setString(3, paymentMethod); // varchar(50)
			ps.setString(4, paymentStatus); // varchar(20)
			ps.setInt(5, amount); // int
			ps.setString(6, transactionId); // varchar(50)

			ps.executeUpdate();

			try (ResultSet rs = ps.getGeneratedKeys()) {
				if (rs.next()) {
					return rs.getInt(1);
				}
			}
		}

		throw new SQLException("Insert order failed, no generated key returned.");
	}

	// 後端查 orders
	// 1.確認訂單是否存在
	// 2.取得 payment_status
	@Override
	public OrderPaymentInfo selectPaymentInfoByOrderId(Connection conn, int orderId) throws SQLException {

		final String sql = "SELECT order_id, payment_status, total_amount " + "FROM orders " + "WHERE order_id = ?";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {

			// 將 orderId 綁定到 SQL 的 ? 參數
			ps.setInt(1, orderId);

			try (ResultSet rs = ps.executeQuery()) {
				if (!rs.next()) {
					// 若查不到訂單，回傳 null
					return null;
				}

				// 建立 OrderPaymentInfo 物件
				OrderPaymentInfo info = new OrderPaymentInfo();
				// 將資料庫查到的欄位值放入 VO
				info.setOrderId(rs.getInt("order_id"));
				info.setPaymentStatus(rs.getString("payment_status"));
				info.setTotalAmount(rs.getInt("total_amount"));
				// 回傳查詢結果給 Service 層
				return info;
			}
		}
	}

	// 後端產生 transaction_id（唯一值）
	// 更新 orders.transaction_id
	// 此方法負責：將產生好的 transactionId 寫回 orders 資料表
	@Override
	public int updateTransactionId(Connection conn, int orderId, String transactionId) throws SQLException {
		// SQL：更新指定 order_id 的 transaction_id
		final String sql = "UPDATE orders SET transaction_id = ? WHERE order_id = ?";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			// SET transaction_id = ?
			ps.setString(1, transactionId);
			// WHERE order_id = ?
			ps.setInt(2, orderId);
			// 回傳更新的筆數
			return ps.executeUpdate();
		}
	}

	@Override
	public String selectTransactionId(Connection conn, String transactionId) throws SQLException {

		final String sql = "SELECT transaction_id FROM orders WHERE transaction_id = ?";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, transactionId);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return rs.getString("transaction_id");
				}
				return null;
			}
		}
	}

	@Override
	public String selectPaymentStatus(Connection conn, String transactionId) throws SQLException {

		final String sql = "SELECT payment_status FROM orders WHERE transaction_id = ?";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, transactionId);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return rs.getString("payment_status");
				}
				return null;
			}
		}
	}

	@Override
	public int updatePaymentStatus(Connection conn, String transactionId, String paymentStatus) throws SQLException {

		final String sql = "UPDATE orders SET payment_status = ? WHERE transaction_id = ?";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, paymentStatus);
			ps.setString(2, transactionId);
			int rows = ps.executeUpdate();
			return rows;
		}
	}
	
	@Override
	public int updateCallbackMeta(Connection conn, String transactionId, String failureReason, String rawResponse) throws SQLException {

	    final String sql = "UPDATE orders SET failure_reason = ?, raw_response = ? WHERE transaction_id = ?";

	    try (PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setString(1, failureReason);
	        ps.setString(2, rawResponse);
	        ps.setString(3, transactionId);
	        return ps.executeUpdate();
	    }
	}
}
