package web.member.controller;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.ResponseBody;
import web.member.exception.BusinessException;
import web.member.service.MemberService;
import web.member.vo.Member;

@Controller
public class LoginController {

	@Autowired
	private MemberService memberService;

	@PostMapping("/login")
	@ResponseBody
	public Map<String, Object> login(@RequestBody Member member, HttpSession session) {
		Member loginMember = memberService.login(member);
		if (loginMember == null) {
			throw new BusinessException("帳號或密碼錯誤，請重新登入!");
		}
		session.setAttribute("member", loginMember);
		return Map.of("success", true, "message", "登入成功!");

	}

}
