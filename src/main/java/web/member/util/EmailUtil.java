package web.member.util;

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

        //從環境變數讀取 
        this.username = System.getenv("MAIL_USERNAME");
        this.password = System.getenv("MAIL_PASSWORD");
        this.host = System.getenv("MAIL_HOST");
        this.port = System.getenv("MAIL_PORT");
        this.baseUrl = System.getenv("APP_BASE_URL");

        //必填檢查 
        if (username == null || password == null) {
            throw new RuntimeException("MAIL_USERNAME 或 MAIL_PASSWORD未設定");
        }

        if (host == null || port == null) {
            throw new RuntimeException("MAIL_HOST 或 MAIL_PORT未設定");
        }

        if (baseUrl == null) {
            throw new RuntimeException("APP_BASE_URL未設定");
        }
    }

    public void sendResetPasswordEmail(String toEmail, String token) {

        //️動態組連結
        String resetLink = baseUrl + "/reset-password.html?token=" + token;

        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

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

            Transport.send(message);

            System.out.println("Email 已寄送給: " + toEmail);

        } catch (MessagingException e) {
            throw new RuntimeException("寄送 Email 失敗", e);
        }
    }
}