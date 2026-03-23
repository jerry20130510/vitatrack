package web.member.controller;

import java.util.Map;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import web.member.dto.DeleteMemberRequest;
import web.member.exception.BusinessException;
import web.member.service.MemberService;
import web.member.vo.Member;

@Controller
public class DeleteAccountController {
	@Autowired
	private MemberService memberService;

	@PostMapping("/deleteAccount")
	@ResponseBody
	public Map<String, Object> delete(HttpSession session, @RequestBody DeleteMemberRequest dto) {
		Member loginMember = (Member) session.getAttribute("member");

		if (loginMember == null) {
			throw new BusinessException("尚未登入");
		}
		memberService.remove(loginMember.getEmail(),dto.getPassword());
		session.invalidate();
		return Map.of("success", true, "message", "帳號已成功註銷");
	}

}
