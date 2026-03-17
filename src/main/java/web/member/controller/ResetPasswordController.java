package web.member.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import web.member.service.PasswordResetTokensService;
import web.member.dto.ResetPasswordRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

@WebServlet("/resetPassword")
public class ResetPasswordController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private PasswordResetTokensService passwordResetTokensService;

	//取得passwordResetTokensService物件
	@Override
	public void init() {
		ApplicationContext applicationContext = WebApplicationContextUtils
				.getWebApplicationContext(getServletContext());
		passwordResetTokensService = applicationContext.getBean(PasswordResetTokensService.class);
	}

	//執行「重設密碼」
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json");
		Gson gson = new Gson();
		JsonObject result = new JsonObject();
		
		try {
			ResetPasswordRequest rpRequest = gson.fromJson(req.getReader(),ResetPasswordRequest.class);
			passwordResetTokensService.resetPassword(rpRequest.getToken(),rpRequest.getNewPassword());
			result.addProperty("success",true );
		} catch (RuntimeException e) {
			result.addProperty("success", false);
			result.addProperty("message", e.getMessage());
		}
		resp.getWriter().write(gson.toJson(result));
	}
}
