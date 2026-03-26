package web.cart.controller;



import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;


import web.cart.dto.ApiResponse;
import web.cart.dto.RemoveCartItemRequest;
import web.cart.dto.RemoveCartItemResponse;

import web.cart.service.CartService;
import web.member.exception.BusinessException;
import web.member.vo.Member;


@Controller
public class RemoveCartItemController {
	
	@Autowired
	private CartService cartService;

	@PostMapping("/removeCartItem")
	@ResponseBody
	public ApiResponse<RemoveCartItemResponse> removeCartItem(@RequestBody RemoveCartItemRequest dto, HttpSession session) {

		Member loginMember = (Member)session.getAttribute("member");
		if (loginMember == null) {
			throw new BusinessException("請先登入會員!");
		}
		Integer loginMemberId = loginMember.getMemberId();	
		
		RemoveCartItemResponse result = cartService.removeItem(loginMemberId, dto.getSkus());
		
		return new ApiResponse<RemoveCartItemResponse>(true, "刪除成功", result);

	}

}
