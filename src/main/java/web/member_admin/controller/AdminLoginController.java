package web.member_admin.controller;

import java.io.IOException;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import web.member.vo.Admin;
import web.member_admin.service.MemberAdminService;
import web.member_admin.service.impl.MemberAdminServiceImpl;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

@WebServlet("/adminLogin")
public class AdminLoginController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private MemberAdminService memberAdminService;

	public AdminLoginController() throws NamingException {
		memberAdminService = new MemberAdminServiceImpl();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json");
		Gson gson = new Gson();
		Admin admin = gson.fromJson(req.getReader(), Admin.class);
		JsonObject result = new JsonObject();
		Admin loginAdmin = memberAdminService.login(admin);

		if (loginAdmin == null) {
			result.addProperty("success", false);
			result.addProperty("message", "帳號或密碼錯誤，請重新登入!");
		} else {
			HttpSession session = req.getSession();
			session.setAttribute("member", loginAdmin);
			result.addProperty("success", true);
			result.addProperty("message", "登入成功!");
		}
		resp.getWriter().write(result.toString());
	}
}
