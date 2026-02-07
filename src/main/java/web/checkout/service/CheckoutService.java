package web.checkout.service;

import web.checkout.vo.CheckoutResult;

public interface CheckoutService {

    CheckoutResult checkout(int memberId);
}
