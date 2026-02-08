package web.checkout.dao;

import java.sql.Connection;
import java.sql.SQLException;

public interface OrderDao {

	int insertOrder(
			Connection conn, 
			int memberId, 
			int totalAmount, 
			String status, 
			String paymentMethod,
			String paymentStatus, 
			int amount, 
			String transactionId) throws SQLException;
}
