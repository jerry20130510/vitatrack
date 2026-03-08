package web.member.controller;

import java.io.IOException;


import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import web.member.service.MemberService;
import web.member.service.impl.MemberServiceImpl;
import web.member.vo.Member;
import web.member_admin.dto.PageResultResponse;
import web.checkout.vo.Orders;
import com.google.gson.Gson;


@WebServlet("/myOrder")
public class MyOrderController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private MemberService memberService;

	public MyOrderController() throws NamingException {

		memberService = new MemberServiceImpl();
	}

	// 查看訂單資料
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		Member member = (Member) session.getAttribute("member");
		int memberId = member.getMemberId();
		int page = Integer.parseInt(req.getParameter("page"));
		int size = 10;
		PageResultResponse<Orders> orderDb = memberService.getMyOrder(memberId,page,size);
		resp.setContentType("application/json");
		Gson gson = new Gson();
		resp.getWriter().write(gson.toJson(orderDb));
	}

	
}
