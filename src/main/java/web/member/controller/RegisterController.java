package web.member.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import web.member.service.MemberService;
import web.member.service.impl.MemberServiceImpl;
import web.member.vo.Member;


@WebServlet("/member/register")
public class RegisterController extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private MemberService memberService;
	
	

	@Override
	public void init() throws ServletException {
		memberService =new MemberServiceImpl();
	}


	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//設定回應資料格式為JSON
		resp.setContentType("Application/json");
		//接收Json物件
		Gson gson =new Gson();
		gson.fromJson(req.getReader(), Member.class);
		
		JsonObject result = new JsonObject();
		
	}

	
}
