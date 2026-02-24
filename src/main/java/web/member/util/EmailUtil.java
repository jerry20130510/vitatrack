package web.member.util;

import java.io.InputStream;
import java.util.Properties;
import jakarta.mail.*;
import jakarta.mail.internet.*;


public class EmailUtil {

	private final String username;
	private final String password;
	private final String host;
	private final String port;
	private final String baseUrl;

	public EmailUtil() {
		try {
			InputStream input = getClass().getClassLoader().getResourceAsStream("mail.properties");

			if (input == null) {
				throw new RuntimeException("找不到 mail.properties");
			}

			Properties mailProps = new Properties();
			mailProps.load(input);

			this.username = mailProps.getProperty("mail.username");
			this.password = mailProps.getProperty("mail.password");
			this.host = mailProps.getProperty("mail.host");
			this.port = mailProps.getProperty("mail.port");
			this.baseUrl = mailProps.getProperty("app.base.url");

			if (username == null || password == null) {
				throw new RuntimeException("MAIL_USERNAME 或 MAIL_PASSWORD未設定");
			}

			if (host == null || port == null) {
				throw new RuntimeException("mail.properties 設定錯誤");
			}

			if (baseUrl == null) {
				throw new RuntimeException("APP_BASE_URL未設定");
			}
			System.out.println("username=" + username);
			System.out.println("password=" + password);
		} catch (Exception e) {
			throw new RuntimeException("讀取 mail.properties 失敗", e);
		}
	}

	public void sendResetPasswordEmail(String toEmail, String token) {

		// ️動態組連結
		String resetLink = baseUrl + "/vitatrack/resetPassword.html?token=" + token;

		Properties props = new Properties();
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		
		String username = System.getenv("MAIL_USERNAME");
		String password = System.getenv("MAIL_PASSWORD");

		Session session = Session.getInstance(props, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
			message.setSubject("密碼重設通知");
			message.setText("請點擊以下連結重設密碼：\n" + resetLink);
			session.setDebug(true);

			Transport.send(message);

			System.out.println("Email 已寄送給: " + toEmail);

		} catch (MessagingException e) {
			  e.printStackTrace(); 
			throw new RuntimeException("寄送 Email 失敗", e);
		}
		
	}
	public static void main(String[] args) {
	    EmailUtil util = new EmailUtil();
	    util.sendResetPasswordEmail("britpop1992@gmail.com", "test-token-123");
	}
}