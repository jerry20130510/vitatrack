package web.member.controller;

import java.io.IOException;
import java.util.List;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import web.member.dto.CartItemResponse;
import web.member.service.MemberService;
import web.member.service.impl.MemberServiceImpl;
import web.member.vo.Member;
import com.google.gson.Gson;


@WebServlet("/myCartItem")
public class MyCartItemController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private MemberService memberService;

	public MyCartItemController() throws NamingException {

		memberService = new MemberServiceImpl();
	}

	// 查看訂單資料
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		Member member = (Member) session.getAttribute("member");
		List<CartItemResponse> cartDb = memberService.viewMyCartItem(member);
		resp.setContentType("application/json");
		Gson gson = new Gson();
		resp.getWriter().write(gson.toJson(cartDb));
	}

}
