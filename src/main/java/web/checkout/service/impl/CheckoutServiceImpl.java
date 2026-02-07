package web.checkout.service.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import web.checkout.dao.CartDao;
import web.checkout.dao.OrderDao;
import web.checkout.dao.OrderItemDao;
import web.checkout.dao.impl.CartDaoImpl;
import web.checkout.dao.impl.OrderDaoImpl;
import web.checkout.dao.impl.OrderItemDaoImpl;
import web.checkout.service.CheckoutService;
import web.checkout.vo.CartRow;
import web.checkout.vo.CheckoutResult;

public class CheckoutServiceImpl implements CheckoutService {

    private final CartDao cartDao = new CartDaoImpl();
    private final OrderDao orderDao = new OrderDaoImpl();
    private final OrderItemDao orderItemDao = new OrderItemDaoImpl();

    private final DataSource ds;

    public CheckoutServiceImpl() {
        try {
            ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/vitatrack");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public CheckoutResult checkout(int memberId) {
        try (Connection conn = ds.getConnection()) {  
            conn.setAutoCommit(false);
        	
            // 1) 查 open cart
            List<CartRow> cartRows = cartDao.findOpenCartByMemberId(memberId);
            if (cartRows == null || cartRows.isEmpty()) {
                throw new RuntimeException("Cart is empty.");
            }

            // 2) 算 total（BigDecimal）
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (CartRow row : cartRows) {
                BigDecimal rowSubtotal = row.getUnitPrice()
                        .multiply(BigDecimal.valueOf(row.getQuantity()));
                totalAmount = totalAmount.add(rowSubtotal);
            }

            // orders.total_amount 目前是 int（MVP 先轉回 int）
            int totalAmountInt = totalAmount.intValue();

            // 3) insert orders（比照你之前 amount / transaction_id 作法）
            String status = "Unpaid";
            String paymentStatus = "PENDING";
            String paymentMethod = "CARD";      // 之後可改成前端傳入
            int amount = totalAmountInt;        // amount = total_amount
            String transactionId = "NA";        // 金流完成再更新真實值

            int orderId = orderDao.insertOrder(
            		conn,
            		memberId,
                    totalAmountInt,
                    status,
                    paymentMethod,
                    paymentStatus,
                    amount,
                    transactionId
            );

            // 4) insert order_item（batch）
            orderItemDao.batchInsertFromCart(conn,orderId, cartRows);

            // 5) 更新 cart_item 綁 order_id
            cartDao.attachCartItemsToOrder(orderId, cartRows);
            return new CheckoutResult(orderId, totalAmountInt, status);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
