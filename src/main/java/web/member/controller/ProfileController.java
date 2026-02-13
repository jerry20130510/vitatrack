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
import web.member.vo.UpdateMemberRequest;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

@WebServlet("/profile")
public class ProfileController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private MemberService memberService;

	public ProfileController() throws NamingException {

		memberService = new MemberServiceImpl();
	}

	// 查看會員資料
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		HttpSession session = req.getSession();
		Member member = (Member) session.getAttribute("member");
		// 除錯點 1：檢查 Service 查完後是不是空的
		System.out.println("Session member: " + session.getAttribute("member"));
		Member profileMember = memberService.profile(member);
		// 除錯點 2：檢查 Service 查完後是不是空的
		System.out.println("Profile Member: " + profileMember);
		resp.setContentType("application/json");
		Gson gson = new Gson();
		// 再轉 Member 物件
		resp.getWriter().write(gson.toJson(profileMember));
	}

	// 修改會員資料
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json");
		Gson gson = new Gson();
		UpdateMemberRequest memberDTO = gson.fromJson(req.getReader(), UpdateMemberRequest.class);
		HttpSession session = req.getSession(false);
		Member loginMember = (Member) session.getAttribute("member");
		if (loginMember == null) {
			throw new RuntimeException("尚未登入");
		}
		JsonObject result = new JsonObject();
		try {
			memberService.updateProfile(loginMember.getMemberId(), memberDTO);
			result.addProperty("success", true);
			result.addProperty("message", "資料更新成功");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			result.addProperty("success", false);
			result.addProperty("message", e.getMessage());
		} catch (RuntimeException e) {
			e.printStackTrace();
			result.addProperty("success", false);
			result.addProperty("message", "系統錯誤");
		}

		resp.getWriter().write(result.toString());
	}
}
