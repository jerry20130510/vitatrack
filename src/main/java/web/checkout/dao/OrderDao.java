package web.checkout.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class OrderDao {

	/**
	 * 建立待付款訂單，回傳 generated order_id
	 */
	public long insertUnpaidOrder(Connection conn, int memberId, long totalAmount) throws Exception {

		String sql = "INSERT INTO orders "
				+ "(member_id, status, total_amount, payment_method, payment_status, amount, transaction_id) "
				+ "VALUES (?, 'Unpaid', ?, 'NA', 'PENDING', ?, 'NA')";

		try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setInt(1, memberId);
			ps.setLong(2, totalAmount);
			ps.setLong(3, totalAmount); // amount 先跟 total_amount 一樣（MVP）

			int affected = ps.executeUpdate();
			if (affected != 1) {
				throw new RuntimeException("Insert orders failed, affected rows=" + affected);
			}

			try (ResultSet rs = ps.getGeneratedKeys()) {
				if (rs.next()) {
					return rs.getLong(1);
				}
			}
		}

		throw new RuntimeException("Insert orders succeeded but no generated key returned.");
	}

	public int insertUnpaid(Connection conn, int memberId, int totalAmount) throws SQLException {

		String sql = "INSERT INTO orders "
				+ "(member_id, status, total_amount, payment_method, payment_status, amount, transaction_id) "
				+ "VALUES (?, 'Unpaid', ?, 'NA', 'PENDING', ?, 'NA')";

		try (PreparedStatement ps = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {

			ps.setInt(1, memberId);
			ps.setInt(2, totalAmount);
			ps.setInt(3, totalAmount);

			int affected = ps.executeUpdate();
			if (affected != 1) {
				throw new SQLException("Insert orders failed, affected rows = " + affected);
			}

			try (ResultSet rs = ps.getGeneratedKeys()) {
				if (rs.next()) {
					return rs.getInt(1); // auto_increment order_id
				}
			}

			throw new SQLException("Insert orders failed, no generated key returned.");
		}
	}

}
