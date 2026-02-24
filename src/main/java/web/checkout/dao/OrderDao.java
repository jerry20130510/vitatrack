package web.checkout.dao;

import java.sql.Connection;
import java.sql.SQLException;

import org.hibernate.Session;

import web.checkout.vo.Orders;
import web.checkout.vo.OrderPaymentInfo;

public interface OrderDao {

	// 新增訂單
	void save(Session session, Orders order);

	// 查訂單
	OrderPaymentInfo selectPaymentInfoByOrderId(Session session, int orderId);

	// 產生一組唯一的 transaction_id
	int updateTransactionId(Session session, int orderId, String transactionId);

	// 檢查 transactionId 是否重複
	boolean existsTransactionId(Session session, String transactionId);

	// 用 transaction_id 查訂單是否存在
	Orders selectByTransactionId(Session session, String transactionId);

}
