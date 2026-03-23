package web.member.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.ResponseBody;
import web.member.service.MemberService;
import web.member.vo.Member;

@Controller
public class LoginController {

	@Autowired
	private MemberService memberService;

	@PostMapping("/login")
	@ResponseBody
	public Map<String, Object> login(@RequestBody Member member, HttpServletRequest request) {
		Member loginMember = memberService.login(member);
		//資安考量
		HttpSession oldSession = request.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate(); 
        }
        
        HttpSession newSession = request.getSession(true);
		newSession.setAttribute("member", loginMember);
		return Map.of("success", true, "message", "登入成功!");

	}

}
