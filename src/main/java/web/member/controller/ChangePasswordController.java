package web.member.controller;

import java.io.IOException;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import web.member.dto.ChangePasswordRequest;
import web.member.service.MemberService;
import web.member.service.impl.MemberServiceImpl;
import web.member.vo.Member;
import com.google.gson.Gson;
import com.google.gson.JsonObject;


@WebServlet("/changePassword")
public class ChangePasswordController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private MemberService memberService;

	public ChangePasswordController() throws NamingException {
		memberService = new MemberServiceImpl();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json");
		Gson gson = new Gson();
		JsonObject result = new JsonObject();
		try {
			HttpSession session = req.getSession();
			Member member =(Member) session.getAttribute("member");
			System.out.println(member);
			if (member == null) {
	            result.addProperty("success", false);
	            result.addProperty("message", "請先登入");
	            resp.getWriter().write(gson.toJson(result));
	            return;
	        }
			ChangePasswordRequest cpr = gson.fromJson(req.getReader(), ChangePasswordRequest.class );
			Boolean isUpdated = memberService.changePassword(member.getEmail(),cpr.getOldPassword(),cpr.getNewPassword());
			if (isUpdated) {
				result.addProperty("success", true);
				result.addProperty("message", "密碼更新成功，請重新登入");
			}
			else {
				result.addProperty("success", false);
	            result.addProperty("message", "舊密碼錯誤，請重新確認");
			}
		} catch (RuntimeException e) {
			result.addProperty("success", false);
			result.addProperty("message", e.getMessage());	
		}
		resp.getWriter().write(gson.toJson(result));
		
	}

}
