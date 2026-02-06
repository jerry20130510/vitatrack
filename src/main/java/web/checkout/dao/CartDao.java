package web.checkout.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import web.checkout.vo.CartRow;

public interface CartDao {

    /**
     * 看購物車
     */
    List<CartRow> findOpenCartByMemberId(int memberId);

    /**
     * 結帳前查購物車
     */
    List<CartRow> findOpenCartByMemberId(Connection conn, int memberId) throws SQLException;

    /**
     * 結帳
     */
    int[] attachCartItemsToOrder(int orderId, List<CartRow> cartRows) throws SQLException;
}
