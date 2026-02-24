package web.checkout.dao;

import java.util.List;

import org.hibernate.Session;

import web.checkout.vo.CartRow;

public interface CartDao {
	
    // 查看尚未結帳的購物車
    List<CartRow> findOpenCartByMemberId(Session session, int memberId);

    // 更新cart_item 的 order_id
    int attachCartItemsToOrder(Session session, int orderId, List<CartRow> cartRows);
}
