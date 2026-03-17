package web.member.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import web.member.service.PasswordResetTokensService;

import web.member.dto.ForgetPasswordRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;


@WebServlet("/forgetPassword")
public class ForgetPasswordController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@Autowired
	private PasswordResetTokensService passwordResetTokensService;

	//取得passwordResetTokensService物件
	@Override
	public void init() {
		ApplicationContext applicationContext = WebApplicationContextUtils
				.getWebApplicationContext(getServletContext());
		passwordResetTokensService = applicationContext.getBean(PasswordResetTokensService.class);
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
