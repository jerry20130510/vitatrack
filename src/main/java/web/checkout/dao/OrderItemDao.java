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
    int[] batchInsertFromCart(
    		Connection conn, 
    		int orderId, 
    		List<CartRow> cartRows)
            throws SQLException;
    // 查詢綠界所需要的 ItemName
    List<String> selectProductNamesByOrderId(Connection conn, int orderId) throws SQLException;

}
