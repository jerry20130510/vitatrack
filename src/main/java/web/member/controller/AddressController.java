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
import com.google.gson.Gson;


@WebServlet("/address")
public class AddressController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private MemberService memberService;

	public AddressController() throws NamingException {

		memberService = new MemberServiceImpl();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		HttpSession session = req.getSession();
		Member member= (Member)session.getAttribute("member");
		Member addressMember =memberService.profile(member);
		// 除錯點 2：檢查 Service 查完後是不是空的
	    System.out.println("Profile Member: " + addressMember);
	    
		resp.setContentType("application/json");
		Gson gson = new Gson();
		// 再轉 Member 物件
		resp.getWriter().write(gson.toJson(addressMember));
	}
}
