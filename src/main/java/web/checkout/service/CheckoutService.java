package web.checkout.service;

import java.util.List;

import web.checkout.vo.CartRow;
import web.checkout.vo.CheckoutResult;

public interface CheckoutService {

	// 將購物車內容呈現在結帳付款頁面
    List<CartRow> getCheckoutCart(int memberId);
	
    //更新訂單相關資料表，包含 orders。order_item、cart_item
	CheckoutResult checkout(int memberId);
    
}
