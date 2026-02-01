package web.checkout;

import web.checkout.dao.CartDao;
import web.checkout.vo.CartRow;

import java.util.List;

public class TestCartDao {

    public static void main(String[] args) {
        CartDao cartDao = new CartDao();

        List<CartRow> rows = cartDao.findOpenCartByMemberId(26);

        System.out.println("Cart size = " + rows.size());
        for (CartRow row : rows) {
            System.out.println(row);
        }
    }
}
