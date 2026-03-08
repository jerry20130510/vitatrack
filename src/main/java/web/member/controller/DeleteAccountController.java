package web.member.controller;

import java.io.IOException;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import web.member.service.MemberService;
import web.member.service.impl.MemberServiceImpl;
import web.member.vo.Member;
import com.google.gson.Gson;
import com.google.gson.JsonObject;


@WebServlet("/deleteAccount")
public class DeleteAccountController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private MemberService memberService;

	public DeleteAccountController() throws NamingException {
		memberService = new MemberServiceImpl();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json");
		Gson gson = new Gson();
		JsonObject result = new JsonObject();
		try {
			Member member = gson.fromJson(req.getReader(),Member.class);
			memberService.remove(member.getEmail());
			result.addProperty("success", true);
			result.addProperty("message", "帳號已刪除成功");
		} catch (RuntimeException e) {
			result.addProperty("success", false);
			result.addProperty("message", e.getMessage());	
		}
		resp.getWriter().write(gson.toJson(result));
		
	}

}
