package cart.service.impl;

import cart.dao.AddToCartDao;
import cart.service.AddToCartService;
import vo.CartItem;

public class AddToCartServiceImpl implements AddToCartService {

    private AddToCartDao addToCartDao;

    public AddToCartServiceImpl(AddToCartDao addToCartDao) {
        this.addToCartDao = addToCartDao;
    }

    @Override
    public void insertCartItem(CartItem item) {

        if (item.getQty() <= 0) {
            throw new IllegalArgumentException("數量需大於 0");
        }

        addToCartDao.insertCartItem(item);
    }
}
