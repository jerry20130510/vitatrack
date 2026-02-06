package web.checkout.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import web.checkout.vo.CartRow;

public interface OrderItemDao {

    /**
     * 依 cartRows 批次新增 order_item
     * @return batch 執行結果
     */
    int[] batchInsertFromCart(Connection conn, int orderId, List<CartRow> cartRows)
            throws SQLException;
}
