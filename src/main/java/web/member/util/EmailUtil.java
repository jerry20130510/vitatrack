package web.member.util;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;



@Component
public class EmailUtil {
	
	@Autowired
	private JavaMailSender mailSender;
	
	public void sendResetPasswordEmail(String email, String token) {
		
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(email);
        message.setSubject("VitaTrack重設密碼通知");
        
        String resetUrl = "http://localhost:8080/vitatrack/member/reset-password?token=" + token;
        message.setText("您好：\n\n請點擊以下連結以重設您的密碼（連結於 15 分鐘內有效）：\n" + resetUrl 
                + "\n\n如果這不是您本人的操作，請忽略此郵件。");
        
        mailSender.send(message);
	}

}