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
import com.google.gson.JsonObject;

@WebServlet("/logout")
public class LogoutController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private MemberService memberService;

	public LogoutController() throws NamingException {

		memberService = new MemberServiceImpl();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json");
		HttpSession session = req.getSession(false);
		if (session!= null) {
			session.invalidate();
		}
		JsonObject result = new JsonObject();
		result.addProperty("success", true);
		result.addProperty("message", "登出成功!");
		resp.getWriter().write(result.toString());
	}
}
