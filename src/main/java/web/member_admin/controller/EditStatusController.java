package web.member_admin.controller;

import java.io.IOException;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import web.member.dto.EditMemberStatusRequest;
import web.member_admin.service.MemberAdminService;
import web.member_admin.service.impl.MemberAdminServiceImpl;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

@WebServlet("/editStatus")
public class EditStatusController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private MemberAdminService memberAdminService;

	public EditStatusController() throws NamingException {
		memberAdminService = new MemberAdminServiceImpl();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json");
		Gson gson = new Gson();
		EditMemberStatusRequest member = gson.fromJson(req.getReader(), EditMemberStatusRequest.class);
		JsonObject result = new JsonObject();
		EditMemberStatusRequest memberDb = memberAdminService.editMemberStatus(member);
		try {
			if (memberDb == null) {
				result.addProperty("success", false);
				result.addProperty("message", "會員狀態更新失敗!");
			} else {
				result.addProperty("success", true);
				result.addProperty("message", "會員狀態更新成功!");
			}
			resp.getWriter().write(result.toString());
		} catch (Exception e) {
			result.addProperty("message", e.getMessage());
			resp.getWriter().write(result.toString());
		}
		
		
	}
}
