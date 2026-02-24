package web.checkout.dao.impl;

import java.util.List;

import org.hibernate.Session;

import web.checkout.dao.OrderItemDao;
import web.checkout.vo.OrderItem;

public class OrderItemDaoImpl implements OrderItemDao {

	// 新增一筆訂單明細
    @Override
    public void save(Session session, OrderItem item) {
        session.persist(item);
    }

    // 查該訂單的商品名稱清單
    @Override
    public List<String> selectProductNamesByOrderId(Session session, int orderId) {

        // 查 OrderItem entity 欄位 productName
        String hql = "SELECT oi.productName FROM OrderItem oi WHERE oi.order.orderId = :oid ORDER BY oi.itemId";

        return session.createQuery(hql, String.class)
                .setParameter("oid", orderId)
                .getResultList();
    }
}