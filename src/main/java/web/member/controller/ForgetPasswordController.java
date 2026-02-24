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
import web.member.service.PasswordResetTokensService;
import web.member.service.impl.MemberServiceImpl;
import web.member.service.impl.PasswordResetTokensServiceImpl;
import web.member.dto.ForgetPasswordRequest;
import web.member.vo.Member;
import web.member.vo.PasswordResetTokens;

import com.google.gson.Gson;
import com.google.gson.JsonObject;


@WebServlet("/forgetPassword")
public class ForgetPasswordController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private PasswordResetTokensService passwordResetTokensService;

	public ForgetPasswordController() throws NamingException {
		passwordResetTokensService = new PasswordResetTokensServiceImpl();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json");
		Gson gson = new Gson();
		JsonObject result = new JsonObject();
		try {
			ForgetPasswordRequest fpRequest = gson.fromJson(req.getReader(),ForgetPasswordRequest.class);
			passwordResetTokensService.createResetToken(fpRequest.getEmail());
			result.addProperty("success", true);
			result.addProperty("message", "已寄出密碼重設連結");
		} catch (RuntimeException e) {
			result.addProperty("success", false);
			result.addProperty("message", e.getMessage());	
		}
		resp.getWriter().write(gson.toJson(result));
		
	}

	
}
