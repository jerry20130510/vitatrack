package web.checkout;

import java.sql.Connection;
import java.util.List;

import core.util.DbUtil;
import web.checkout.dao.CartDao;
import web.checkout.dao.OrderItemDao;
import web.checkout.vo.CartRow;

public class TestOrderItemDao {

    public static void main(String[] args) {

        int memberId = 26;

        // TODO: 換成 orders 表裡 реально存在的一筆 order_id
        // 先用 Workbench 查：
        // SELECT order_id FROM orders ORDER BY order_id DESC LIMIT 5;
        int existingOrderId = 1;

        CartDao cartDao = new CartDao();
        OrderItemDao orderItemDao = new OrderItemDao();

        try (Connection conn = DbUtil.getConnection()) {
            conn.setAutoCommit(false);

            // 1) 用同一個 conn 查購物車（order_id IS NULL）
            List<CartRow> cartRows = cartDao.findOpenCartByMemberId(conn, memberId);
            System.out.println("Cart size = " + cartRows.size());
            for (CartRow r : cartRows) {
                System.out.println(r);
            }

            if (cartRows.isEmpty()) {
                System.out.println("Cart is empty. Stop.");
                conn.rollback();
                return;
            }

            // 2) 寫入 order_item（batch）
            int[] results = orderItemDao.insertBatch(conn, existingOrderId, cartRows);

            // 3) 印出 batch 執行結果
            System.out.println("Batch insert results length = " + results.length);
            for (int i = 0; i < results.length; i++) {
                System.out.println("row " + i + " affected = " + results[i]);
            }

            // 4) 測試用：不真的寫入
            conn.rollback();
            System.out.println("Rollback done. (No data inserted)");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
