package web.checkout.dao;

import java.sql.Connection;
import java.sql.SQLException;

import web.checkout.vo.OrderPaymentInfo;

public interface OrderDao {
	// 新增訂單
	int insertOrder(
			Connection conn, 
			int memberId, 
			int totalAmount, 
			String status, 
			String paymentMethod,
			String paymentStatus, 
			int amount, 
			String transactionId) throws SQLException;
	
	// 後端查 orders
	// 1. 確認訂單是否存在
	// 2. 取得 payment_status
	OrderPaymentInfo selectPaymentInfoByOrderId(
	        Connection conn,
	        int orderId) throws SQLException;
	 
	 //	產生一組唯一的 transaction_id
	 int updateTransactionId(Connection conn, int orderId, String transactionId) throws SQLException;

}
