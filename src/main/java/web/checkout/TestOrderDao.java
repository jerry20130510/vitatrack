package web.checkout;

import core.util.DbUtil;
import web.checkout.dao.OrderDao;

import java.sql.Connection;

public class TestOrderDao {

    public static void main(String[] args) {
        // ✅ 你的 seed 資料：member_id = 26
        int memberId = 26;

        // ✅ 先用你現在購物車的兩筆商品算總額（之後會改成從 CartDao 算）
        long totalAmount = 32900 + 7490 * 2; // iPhone15(1) + AirPodsPro(2)

        try (Connection conn = DbUtil.getConnection()) {
            conn.setAutoCommit(false); // ✅ 測試時先手動控制 commit

            OrderDao orderDao = new OrderDao();

            long orderId = orderDao.insertUnpaidOrder(conn, memberId, totalAmount);

            conn.commit();

            System.out.println("✅ orderId = " + orderId);
            System.out.println("✅ totalAmount = " + totalAmount);
            System.out.println("✅ status = Unpaid");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
