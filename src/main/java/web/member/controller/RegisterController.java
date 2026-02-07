package web.member.controller;

import java.io.IOException;
import javax.naming.NamingException;
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

@WebServlet("/register")
public class RegisterController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private MemberService memberService;

	@Override
	public void init() throws ServletException {
		try {
			memberService = new MemberServiceImpl();
		} catch (NamingException e) {

			e.printStackTrace();
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		// 設定回應資料格式為JSON
		resp.setContentType("application/json;charset=utf-8");
		
		// 接收Json物件
		Gson gson = new Gson();
		JsonObject result = new JsonObject();

		Member member = gson.fromJson(req.getReader(), Member.class);

		// 調用service層的方法
		//最終結果：Service 最終回傳 null 給 Controller，Controller 看到是 null 就導向「註冊成功頁面」。
		String errMsg = memberService.register(member);

		if (errMsg == null) {
			result.addProperty("success", true);  // <--- 這裡定義了 key 名稱為 "success"
			result.addProperty("message", "註冊成功");
		} else {
			result.addProperty("success", false);
			result.addProperty("message", errMsg);
		}
		//輸出純文字的json格式
		resp.getWriter().write(result.toString());
	}

}
