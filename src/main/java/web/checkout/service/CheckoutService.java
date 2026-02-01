package web.checkout.service;

import java.sql.Connection;
import java.util.List;

import core.util.DbUtil;
import web.checkout.dao.CartDao;
import web.checkout.dao.OrderDao;
import web.checkout.dao.OrderItemDao;
import web.checkout.vo.CartRow;
import web.checkout.vo.CheckoutResult;

public class CheckoutService {

    private final CartDao cartDao = new CartDao();
    private final OrderDao orderDao = new OrderDao();
    private final OrderItemDao orderItemDao = new OrderItemDao();

    public CheckoutResult checkout(int memberId) {

        try (Connection conn = DbUtil.getConnection()) {
            conn.setAutoCommit(false);

            try {
                // 1) 查購物車（同一個 conn）
                List<CartRow> cartRows = cartDao.findOpenCartByMemberId(conn, memberId);
                if (cartRows.isEmpty()) {
                    throw new RuntimeException("Cart is empty.");
                }

                // 2) 計算 totalAmount
                int totalAmount = 0;
                for (CartRow r : cartRows) {
                    totalAmount += r.getUnitPrice() * r.getQuantity();
                }

                // 3) 建立 orders（Unpaid + 占位值）
                int orderId = orderDao.insertUnpaid(conn, memberId, totalAmount);

                // 4) 寫 order_item（batch insert）
                orderItemDao.insertBatch(conn, orderId, cartRows);

                // 5) 更新 cart_item：把這些 cart_item 綁到 orderId（batch update）
                cartDao.attachCartItemsToOrder(conn, orderId, cartRows);

                // 6) commit
                conn.commit();

                return new CheckoutResult(orderId, totalAmount, "Unpaid");

            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            }

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
