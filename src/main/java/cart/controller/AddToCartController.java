package cart.controller;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cart.dao.impl.AddToCartDaoImpl;
import cart.dao.impl.CartDaoImpl;
import cart.service.AddToCartService;
import cart.service.CartService;
import cart.service.impl.AddToCartServiceImpl;
import cart.service.impl.CartServiceImpl;
import product.dao.impl.ProductDaoImpl;
import product.service.ProductService;
import product.service.impl.ProductServiceImpl;
import vo.CartItem;

@WebServlet("/cart/add")
public class AddToCartController extends HttpServlet {

	private AddToCartService addToCartService;

	 @Override
	    public void init() {

	        addToCartService =
	            new AddToCartServiceImpl(new AddToCartDaoImpl());
	    }
	
//	 @Override
//	    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
//	            throws IOException {
//
//	        String productIdStr = req.getParameter("productId");
//	        if (productIdStr == null) {
//	            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//	            return;
//	        }
//
//	        int productId = Integer.parseInt(productIdStr);
//	        int stock = addToCartService.getProductStock(productId);
//
//	        resp.setContentType("application/json;charset=UTF-8");
//	        resp.getWriter().write("{\"stock\":" + stock + "}");
//	    }

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		CartItem item = new CartItem();
		
		 item.setProductId(Integer.parseInt(req.getParameter("productId")));
		 item.setQty(Integer.parseInt(req.getParameter("quantity")));

		addToCartService.insertCartItem(item);

		resp.setStatus(HttpServletResponse.SC_OK);
	}
}
