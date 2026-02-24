package web.checkout.dao;

import java.util.List;

import org.hibernate.Session;

import web.checkout.vo.OrderItem;

public interface OrderItemDao {

	// 新增一筆訂單明細
	void save(Session session, OrderItem item);

	// 查該訂單的商品名稱清單
	List<String> selectProductNamesByOrderId(Session session, int orderId);

}