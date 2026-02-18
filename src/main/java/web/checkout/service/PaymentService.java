package web.checkout.service;

import java.sql.Connection;
import java.sql.SQLException;

import web.checkout.vo.EcpayCheckoutPayload;
import web.checkout.vo.OrderPaymentInfo;

public interface PaymentService {

	//	後端查 orders並檢查 payment_status
	//	訂單不存在：回傳 null 或丟例外
	//	payment_status = SUCCESS,不可再付款（丟例外）
	//	payment_status = PENDING / FAILED,允許付款（回傳訂單資訊）
    OrderPaymentInfo validateOrderCanPay(Connection conn, int orderId) throws SQLException;
    
    // 組綠界所需 payload (actionUrl + formParams)
 	// 先呼叫 validateOrderCanPay()
 	// CheckMacValue 先空字串，之後再補
    EcpayCheckoutPayload createEcpayCheckout(Connection conn, int orderId) throws SQLException;
}
