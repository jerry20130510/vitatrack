package web.checkout.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.Session;
import org.hibernate.query.Query;

import web.checkout.dao.OrderDao;
import web.checkout.vo.Orders;
import web.checkout.vo.OrderPaymentInfo;

public class OrderDaoImpl implements OrderDao {

	// 新增訂單
	@Override
	public void save(Session session, Orders order) {
		session.persist(order);
	}

	// 查訂單
	@Override
	public OrderPaymentInfo selectPaymentInfoByOrderId(Session session, int orderId) {

		Orders o = session.get(Orders.class, orderId);
		if (o == null)
			return null;

		OrderPaymentInfo info = new OrderPaymentInfo();
		info.setOrderId(o.getOrderId());
		info.setPaymentStatus(o.getPaymentStatus());

		if (o.getTotalAmount() != null) {
			info.setTotalAmount(o.getTotalAmount().intValue());
		} else {
			info.setTotalAmount(0);
		}

		info.setTransactionId(o.getTransactionId());

		return info;
	}

	// 產生一組唯一的 transaction_id
	@Override
	public int updateTransactionId(Session session, int orderId, String transactionId) {

		String hql = "UPDATE Orders o SET o.transactionId = :txid WHERE o.orderId = :oid";

		Query<?> q = session.createQuery(hql);
		q.setParameter("txid", transactionId);
		q.setParameter("oid", orderId);

		return q.executeUpdate();
	}

	// 檢查 transactionId 是否重複
	@Override
	public boolean existsTransactionId(Session session, String transactionId) {

		String hql = "SELECT 1 FROM Orders o WHERE o.transactionId = :txid";

		Integer one = session.createQuery(hql, Integer.class).setParameter("txid", transactionId).setMaxResults(1)
				.uniqueResult();

		return one != null;
	}
	
	// 用 transaction_id 查訂單是否存在
	@Override
	public Orders selectByTransactionId(Session session, String transactionId) {

		String hql = "FROM Orders o WHERE o.transactionId = :txid";

		return session.createQuery(hql, Orders.class)
				.setParameter("txid", transactionId)
				.setMaxResults(1)
				.uniqueResult();
	}

}
