package web.member.controller;


import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;


import web.member.service.PasswordResetTokensService;
import web.member.dto.ResetPasswordRequest;


@Controller
public class ResetPasswordController  {
	@Autowired
	private PasswordResetTokensService passwordResetTokensService;
	//執行「重設密碼」
	@PostMapping("/resetPassword")
	@ResponseBody
	public  Map<String, Object> resetPassword(@RequestBody ResetPasswordRequest dto) {
		
		passwordResetTokensService.resetPassword(dto.getToken(),dto.getNewPassword());
		return Map.of("success",true,"message", "密碼重設成功");
	}
}
