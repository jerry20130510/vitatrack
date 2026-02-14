package web.cart.service.impl;

import web.cart.dao.AddToCartDao;
import web.cart.dao.impl.AddToCartDaoImpl;
import web.cart.service.AddToCartService;

public class AddToCartServiceImpl implements AddToCartService {

	   private AddToCartDao addToCartDao = new AddToCartDaoImpl();

	    @Override
	    public void addToCart(int productId, int quantity) {

	        addToCartDao.addToCart(productId, quantity);
	    }
}
