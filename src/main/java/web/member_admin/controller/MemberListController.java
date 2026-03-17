package web.member_admin.controller;

import java.io.IOException;


import javax.naming.NamingException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import web.member_admin.dto.MemberListResponse;
import web.member_admin.dto.PageResultResponse;
import web.member_admin.service.MemberAdminService;
import web.member_admin.service.impl.MemberAdminServiceImpl;
import com.google.gson.Gson;

@WebServlet("/memberList")
public class MemberListController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private MemberAdminService memberAdminService;

	public MemberListController() throws NamingException {

		memberAdminService = new MemberAdminServiceImpl();
	}

	// 查看會員資料
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json");
		Gson gson = new Gson();
		int page = Integer.parseInt(req.getParameter("page"));
		int size = 10;
		try {
			// PageResultResponse
			PageResultResponse<MemberListResponse> result= memberAdminService.getMemberInfo(page, size);
			resp.getWriter().write(gson.toJson(result));
		} catch (Exception e) {
			e.printStackTrace();
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

}
