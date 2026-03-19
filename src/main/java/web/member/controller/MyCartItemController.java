package web.member.controller;


import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import web.member.dto.CartItemResponse;
import web.member.service.MemberService;
import web.member.vo.Member;


@Controller
public class MyCartItemController  {
	
	@Autowired
	private MemberService memberService;

	//查看訂單資料
	@GetMapping("/myCartItem")
	@ResponseBody
	public List<CartItemResponse> getMyCartItem(HttpSession session) {
	
		Member member = (Member) session.getAttribute("member");
		List<CartItemResponse> cartDb = memberService.viewMyCartItem(member);
		return cartDb ;
	}

}
