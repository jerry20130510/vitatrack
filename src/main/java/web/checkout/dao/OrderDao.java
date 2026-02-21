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
	 
	 // 用 transaction_id 查是否存在（存在就回傳該 transaction_id，不存在回 null）
	 String selectTransactionId(Connection conn, String transactionId) throws SQLException;

	 // 用 transaction_id 查 payment_status
	 String selectPaymentStatus(Connection conn, String transactionId) throws SQLException;

	 // 更新 payment_status
	 int updatePaymentStatus(Connection conn, String transactionId, String paymentStatus) throws SQLException;

	 // 更新 raw_response、failureReason
	 int updateCallbackMeta(Connection conn, String transactionId, String failureReason, String rawResponse) throws SQLException;
}
