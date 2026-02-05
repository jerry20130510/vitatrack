package cart.service;

import vo.CartItem;

public interface AddToCartService {
	
    void insertCartItem(CartItem item);
}
