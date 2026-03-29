package web.member.controller;

import java.util.Map;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import web.member.dto.ChangePasswordRequest;
import web.member.exception.BusinessException;
import web.member.service.MemberService;
import web.member.vo.Member;

@Controller
public class ChangePasswordController {

	@Autowired
	private MemberService memberService;

	@PostMapping("/changePassword")
	@ResponseBody
	public Map<String, Object> changePassword(HttpSession session, @RequestBody ChangePasswordRequest dto) {
		Member member = (Member) session.getAttribute("member");
		if (member == null) {
			throw new BusinessException("請先登入");
		}
		memberService.changePassword(member.getEmail(), dto.getOldPassword(), dto.getNewPassword(),dto.getConfirmPassword());
		session.invalidate();
		return Map.of("success", true, "message", "密碼更新成功，請重新登入");
	}

}
