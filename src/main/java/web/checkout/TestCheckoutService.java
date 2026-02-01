package web.checkout;

import web.checkout.service.CheckoutService;
import web.checkout.vo.CheckoutResult;

public class TestCheckoutService {

    public static void main(String[] args) {

        int memberId = 26;

        CheckoutService service = new CheckoutService();

        try {
            CheckoutResult result = service.checkout(memberId);
            System.out.println("Checkout OK => " + result);

            // 你也可以分開印
            System.out.println("orderId = " + result.getOrderId());
            System.out.println("totalAmount = " + result.getTotalAmount());
            System.out.println("status = " + result.getStatus());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
