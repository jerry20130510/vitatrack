package cart.service.impl;

import cart.dao.CartDao;
import cart.service.CartService;

public class CartServiceImpl implements CartService {

    private CartDao cartDao;

    public CartServiceImpl(CartDao cartDao) {
        this.cartDao = cartDao;
    }

    @Override
    public void addItem(int userId, int productId, int quantity) {
        cartDao.addItem(userId, productId, quantity);
    }
}
