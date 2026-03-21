package web.member_admin.controller;


import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import web.member.dto.EditMemberStatusRequest;
import web.member.exception.BusinessException;
import web.member_admin.service.MemberAdminService;



@Controller
public class EditStatusController  {
	@Autowired
	private MemberAdminService memberAdminService;
	
	//編輯會員狀態
	@PostMapping("/editStatus")
	@ResponseBody 
	public Map<String,Object> EditStatus (@RequestBody EditMemberStatusRequest editMember ){
		EditMemberStatusRequest memberDb = memberAdminService.editMemberStatus(editMember);
		if (memberDb == null) {
			throw new BusinessException("會員狀態更新失敗!");
		}
		return Map.of("success", true,"message", "會員狀態更新成功!");		
	}
	
		
}
