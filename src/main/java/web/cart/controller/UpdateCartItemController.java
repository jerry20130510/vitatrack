package web.cart.controller;



import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;


import web.cart.dto.ApiResponse;
import web.cart.dto.UpdateCartItemRequest;
import web.cart.dto.UpdateCartItemResponse;
import web.cart.service.CartService;
import web.member.exception.BusinessException;
import web.member.vo.Member;


@Controller
public class UpdateCartItemController {
	@Autowired
	private CartService cartService;

	@PostMapping("/updateCartItem")
	@ResponseBody
	public ApiResponse<UpdateCartItemResponse> updateCartItem(@RequestBody UpdateCartItemRequest dto, HttpSession session) {

		Member loginMember = (Member)session.getAttribute("member");
		if (loginMember == null) {
			throw new BusinessException("請先登入會員!");
		}
		Integer loginMemberId = loginMember.getMemberId();	
		
		UpdateCartItemResponse result = cartService.updateQuantity(loginMemberId, dto.getSku(), dto.getQuantity());
		
		return new ApiResponse<UpdateCartItemResponse>(true, "更新成功", result);

	}

}
