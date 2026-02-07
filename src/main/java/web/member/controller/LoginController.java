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
import com.google.gson.JsonObject;

@WebServlet("/login")
public class LoginController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private MemberService memberService;

	public LoginController() throws NamingException {
		memberService = new MemberServiceImpl();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json");
		Gson gson = new Gson();
		// 再轉 Member 物件
		Member member = gson.fromJson(req.getReader(), Member.class);

		JsonObject result = new JsonObject();
		Member loginMember = memberService.login(member);

		if (loginMember == null) {
			result.addProperty("success", false);
			result.addProperty("message", "帳號或密碼錯誤，請重新登入!");
		} else {
			HttpSession session = req.getSession();
			session.setAttribute("member", loginMember);

			result.addProperty("success", true);
			result.addProperty("message", "登入成功!");
		}

		resp.getWriter().write(result.toString());
	}
}
