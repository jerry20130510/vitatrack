package web.checkout.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Service;

import core.util.HibernateUtil;
import web.checkout.dao.CartDao;
import web.checkout.dao.OrderDao;
import web.checkout.dao.OrderItemDao;
import web.checkout.service.CheckoutService;
import web.checkout.vo.CartRow;
import web.checkout.vo.CheckoutResult;
import web.checkout.vo.OrderItem;
import web.checkout.vo.Orders;

@Service
public class CheckoutServiceImpl implements CheckoutService {

	private final CartDao cartDao;
	private final OrderDao orderDao;
	private final OrderItemDao orderItemDao;

	public CheckoutServiceImpl(CartDao cartDao, OrderDao orderDao, OrderItemDao orderItemDao) {
        this.cartDao = cartDao;
        this.orderDao = orderDao;
        this.orderItemDao = orderItemDao;
    }
	
	@Override
	public CheckoutResult checkout(int memberId) {

		Session session = null;
		// 開始 Transaction
		Transaction tx = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			// 1.查詢 open cart
			List<CartRow> cartRows = cartDao.findOpenCartByMemberId(session, memberId);
			// 1.1如果查不到，顯示Cart is empty.
			if (cartRows == null || cartRows.isEmpty()) {
				throw new RuntimeException("Cart is empty.");
			}

			// 2.計算總金額
			BigDecimal totalAmount;
			totalAmount = BigDecimal.valueOf(0);
			for (CartRow row : cartRows) {
				// 2.1 取得單價
				BigDecimal price = row.getUnitPrice();

				// 2.2 取得數量
				int quantity = row.getQuantity();

				// 2.3 將 quantity 轉成 BigDecimal
				BigDecimal quantityBD = BigDecimal.valueOf(quantity);

				// 2.4 單價 × 數量
				BigDecimal rowSubtotal = price.multiply(quantityBD);
				totalAmount = totalAmount.add(rowSubtotal);
			}
			// 2.5 將 BigDecimal 轉成 int
			int totalAmountInt = totalAmount.intValue();

			// 3.建立 orders
			Orders order = new Orders();
			order.setMemberId(memberId);
			order.setTotalAmount(totalAmount);
			order.setPaymentStatus("PENDING");
			order.setPaymentMethod("ECPAY");
			order.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
			order.setAmount(totalAmount);
			order.setTransactionId("TXN" + System.currentTimeMillis());
			order.setPaymentTime(new java.sql.Timestamp(System.currentTimeMillis()));

			orderDao.save(session, order);

			// 4.建立 order_item
			Integer orderId = order.getOrderId();
			for (CartRow row : cartRows) {
				OrderItem oi = new OrderItem();
				// 4.1 order_id
				oi.setOrder(order);
				// 4.2 sku
				oi.setSku(row.getSku());
				// 4.3 product_name
				oi.setProductName(row.getProductName());
				// 4.4 unit_price
				oi.setUnitPrice(row.getUnitPrice());
				// 4.5 quantity
				oi.setQuantity(row.getQuantity());
				// 4.6 subtotal
				BigDecimal price = row.getUnitPrice();
				int quantity = row.getQuantity();
				BigDecimal quantityBD = BigDecimal.valueOf(quantity);
				BigDecimal subtotal = price.multiply(quantityBD);
				oi.setSubtotal(subtotal);
				orderItemDao.save(session, oi);
			}

			// 5.更新 cart_item.order_id
			int updatedCount = cartDao.attachCartItemsToOrder(session, orderId, cartRows);
			if (updatedCount <= 0) {
				throw new RuntimeException("attachCartItemsToOrder updated 0 rows.");
			}

			// 6.提交 Transaction
			tx.commit();
			// 7.回傳 CheckoutResult
			return new CheckoutResult(orderId, totalAmountInt, "PENDING");

		} catch (Exception ex) {
			if (tx != null) {
				try {
					tx.rollback();
				} catch (Exception ignore) {
				}
			}

			// 🔥 找出最底層錯誤原因
			Throwable root = ex;
			while (root.getCause() != null) {
				root = root.getCause();
			}

			System.out.println("=================================");
			System.out.println("🔥 ROOT CAUSE CLASS: " + root.getClass().getName());
			System.out.println("🔥 ROOT CAUSE MESSAGE: " + root.getMessage());
			System.out.println("=================================");

			ex.printStackTrace();

			return new CheckoutResult(0, 0, "FAILED: " + root.getMessage());
		} finally {
			if (session != null) {
				try {
					session.close();
				} catch (Exception ignore) {
				}
			}
		}
	}

	@Override
	public List<CartRow> getCheckoutCart(int memberId) {
		Session session = null;
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			return cartDao.findOpenCartByMemberId(session, memberId);
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

}
