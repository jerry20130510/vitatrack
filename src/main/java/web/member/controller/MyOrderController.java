package web.member.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import web.member.service.MemberService;
import web.member.vo.Member;
import web.member_admin.dto.PageResultResponse;
import web.checkout.vo.Orders;

@Controller
public class MyOrderController {

	@Autowired
	private MemberService memberService;

	// 查看訂單資料
	@GetMapping("/myOrder")
	@ResponseBody
	public PageResultResponse<Orders> getMyOrder(HttpSession session, @RequestParam("page") int page) {

		Member member = (Member) session.getAttribute("member");
		int size = 10;
		PageResultResponse<Orders> orderDb = memberService.viewMyOrder(member, page, size);
		return orderDb;
	}

}
