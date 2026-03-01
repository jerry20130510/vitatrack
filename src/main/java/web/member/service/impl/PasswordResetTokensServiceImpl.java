package web.member.service.impl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.naming.NamingException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import core.util.HibernateUtil;
import web.member.dao.MemberDao;
import web.member.dao.PasswordResetTokensDao;
import web.member.dao.impl.MemberDaoImpl;
import web.member.dao.impl.PasswordResetTokensDaoImpl;

import web.member.service.PasswordResetTokensService;
import web.member.util.EmailUtil;
import web.member.vo.Member;
import web.member.vo.PasswordResetTokens;

public class PasswordResetTokensServiceImpl implements PasswordResetTokensService {
	private MemberDao memberDao;
	private PasswordResetTokensDao passwordResetTokenDao;
	private EmailUtil emailUtil;

	public PasswordResetTokensServiceImpl() throws NamingException {
		memberDao = new MemberDaoImpl();
		passwordResetTokenDao = new PasswordResetTokensDaoImpl();
		emailUtil = new EmailUtil(); // 初始化 EmailUtil
	}

	@Override
	public void createResetToken(String email) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Transaction tx = session.beginTransaction();
		String token = null;
		try {
			Member member = memberDao.selectByEmail(email);
			if (member == null) {
				tx.commit();
				return;
			}
			token = UUID.randomUUID().toString();
			PasswordResetTokens resetToken = new PasswordResetTokens();
			resetToken.setMemberId(member.getMemberId());
			resetToken.setToken(token);
			resetToken.setUsed(false);
			resetToken.setCreateTime(new Timestamp(System.currentTimeMillis()));
			resetToken.setExpiryTime(Timestamp.valueOf(LocalDateTime.now().plusMinutes(15)));

			passwordResetTokenDao.insert(resetToken);
			tx.commit();
			System.out.println(token);
		} catch (Exception e) {
			tx.rollback();
			throw new RuntimeException(e);
		}
		// transaction commit 成功後再寄信
		if (token != null) {
			emailUtil.sendResetPasswordEmail(email, token);
		}

	}

	// 確認這個Token是否合法、有效、可使用
	@Override
	public PasswordResetTokens validateToken(String token) {
		// 避免後續DAO查詢時出現不必要錯誤或無效查詢
		if (token == null || token.trim().isEmpty()) {

			throw new IllegalArgumentException("Token不可為空");
		}
		PasswordResetTokens resetToken;
		resetToken = passwordResetTokenDao.findByToken(token);
		if (resetToken == null) {
			throw new IllegalArgumentException("Token無效或不存在");
		}
		// 為避免重複使用同一個重設連結，檢查token使否使用過
		if (resetToken.getUsed()) {
			throw new IllegalArgumentException("Token已使用過");
		}
		// 是否過期;System.currentTimeMillis():取得現在時間（毫秒）
		// before(now) 表示「是否早於現在時間」。若為 true，代表已過期。
		Timestamp now = new Timestamp(System.currentTimeMillis());
		if (resetToken.getExpiryTime().before(now)) {
			throw new IllegalArgumentException("Token已過期");
		}
		return resetToken;
	}

	@Override
	public void resetPassword(String token, String newPassword) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			// 驗證token
			PasswordResetTokens resetToken = validateToken(token);
			if (resetToken == null) {
				throw new IllegalArgumentException("Token無效");
			}
			// 撈出會員資料;session.get() 從資料庫抓出對應的Member實體
			Member member = session.get(Member.class, resetToken.getMemberId());
			if (member == null) {
				throw new IllegalArgumentException("找不到對應會員");
			}
			// 驗證密碼格式
			if (newPassword == null || !newPassword.matches("^(?=.*[A-Za-z])(?=.*\\d).{8,}$")) {
				throw new IllegalArgumentException("密碼格式錯誤或未填寫!");
			}
			// 更新會員密碼
			member.setPassword(newPassword);
			// 標記token已使用
			resetToken.setUsed(true);
			passwordResetTokenDao.update(resetToken);
			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			if (tx != null) {
				tx.rollback();
			}
			throw new RuntimeException("密碼重設失敗", e);
		}
	}

}
