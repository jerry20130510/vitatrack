package web.member_admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import web.member_admin.dto.MemberListResponse;
import web.member_admin.dto.PageResultResponse;
import web.member_admin.service.MemberAdminService;

//@param page 起始頁碼（預設從1開始)
//@param size 每頁筆數（預設10筆）

@Controller
public class MemberSearchController {
	@Autowired
	private MemberAdminService memberAdminService;

	// 關鍵字查詢會員
	@GetMapping("/memberSearch")
	@ResponseBody
	public PageResultResponse<MemberListResponse> memberList(
			@RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "size", defaultValue = "10") int size) {

		return memberAdminService.searchMemberInfo(keyword, page, size);

	}
}
