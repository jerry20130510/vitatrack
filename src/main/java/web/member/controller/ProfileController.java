package web.member.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import web.member.service.MemberService;
import web.member.vo.Member;
import web.member.dto.MemberProfileResponse;
import web.member.dto.UpdateMemberRequest;
import web.member.exception.BusinessException;


@Controller
public class ProfileController {

	@Autowired
	private MemberService memberService;

	// 查看會員
	@GetMapping("/getProfile")
	@ResponseBody
	public MemberProfileResponse profile(HttpSession session) {

		Member member = (Member) session.getAttribute("member");
		if (member == null) {
	        throw new BusinessException("請先登入");
	    }
		Member profileMember = memberService.profile(member);
		MemberProfileResponse result = new MemberProfileResponse(profileMember);
		return result;
	}

	// 修改會員
	@PostMapping("/updateProfile")
	@ResponseBody
	public MemberProfileResponse updateProfile(@RequestBody UpdateMemberRequest memberDTO, HttpSession session) {

		Member loginMember = (Member) session.getAttribute("member");
		Member updatedMember = memberService.updateProfile(loginMember, memberDTO);
		return new MemberProfileResponse(updatedMember);
	}

}
