package web.member.service.impl;

import javax.naming.NamingException;

import org.hibernate.Session;
import org.hibernate.Transaction;

import core.util.HibernateUtil;
import web.member.dao.MemberDao;
import web.member.dao.impl.MemberDaoImpl;
import web.member.service.MemberService;
import web.member.vo.Member;


public class MemberServiceImpl implements MemberService {
	private MemberDao memberDao;

	public MemberServiceImpl() throws NamingException {
		memberDao = new MemberDaoImpl();
	}

	@Override
	public String register(Member member) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Transaction tx = null;
		try {
			tx= session.beginTransaction();
			// 1 姓名不能空白
			String name = member.getName();
			if (name == null || name.trim().isEmpty()) {
				return "會員名為必填欄位!";
			}
			// 2 電子郵件 必須為^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$
			String email = member.getEmail();
			if (email == null || !email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
				return "email格式錯誤或未填寫!";
			}
			// 3 手機 必須是09開頭且共10位數字("^09[0-9]{8}$")
			String phone = member.getPhone();
			if (phone == null || !phone.matches("^09[0-9]{8}$")) {
				return "手機號碼格式錯誤或未填寫!";
			}
			// 4處理地址 (因為是非必填，先處理空值)
			String address = member.getAddress();
			if (address != null) {
				address = address.trim(); // 去除前後空白

				// 限制最大長度，資料庫 VARCHAR(255)，這裡就設 200
				if (address.length() > 200) {
					return "地址長度過長";
				}
				// 如果是空字串，可以統一設為 null 存入資料庫
				if (address.isEmpty()) {
					member.setAddress(null);
				} else {
					// 2. 安全性過濾：過濾掉 HTML 標籤防止 XSS
					// 建議使用外部 Library 如 Jsoup，或簡單用 String 取代
					String safeAddress = address.replaceAll("<[^>]*>", ""); // 防止XSS注入攻擊
					member.setAddress(safeAddress);
				}
			}
			// 5 密碼 密碼至少為 8 個字元，且至少包含 1 個英文字母(大小寫皆可)與 1 個數字
			String password = member.getPassword();
			if (password == null || !password.matches("^(?=.*[A-Za-z])(?=.*\\d).{8,}$")) {
				return "密碼格式錯誤或未填寫!";
			}
			// 6 重新輸入密碼 和密碼 必須一致
			String confirmPassword = member.getConfirmPassword();
			if (!confirmPassword.equals(password)) {
				return "與設定密碼不一致，請重新輸入!";
			}
			// 7 判斷帳號是否有重複，資料庫的email不能等於新註冊的email
			// 邏輯觀念錯誤 以及 語法回傳值不符。
			// 正確邏輯應該是:檢查資料庫裡「是否已經存在這個會員物件」。如果查出來的結果 不是 null，代表這個 Email 已經被註冊過了。
			if (memberDao.selectByEmail(email) != null) {
				return "此帳號已經被註冊了";
			}
			// 8註冊方法在通過所有驗證後，呼叫 memberDao.insert(member)，新增資料。
		    memberDao.insert(member);
			tx.commit();
			return null;
		} catch (Exception e) {
			
			if (tx != null) {
				tx.rollback();
				e.printStackTrace();
				return "系統錯誤，註冊失敗!";
			}
		}
		// 9全部成功，回傳 null 代表沒有錯誤訊息或 代表的是 「錯誤訊息為空」。
		return null;
	}

	@Override
	public Member login(Member member) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Transaction tx = null;
		// 驗證帳號和密碼
		try {
			tx = session.beginTransaction();
			String email = member.getEmail();
			String password = member.getPassword();
			if (email == null || email.isEmpty()) {
				return null;
			}
			if (password == null || password.isEmpty()) {
				return null;
			}
			member = memberDao.SelectByEmailandPassword(email, password);
			tx.commit();
			return member;
		} catch (Exception e) {
			tx.rollback();
			e.printStackTrace();
		}
		return member;

	}

	@Override
	public Member profile(Member member) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Transaction tx = null;
		// 顯示當前會員資料
		try {
			tx = session.beginTransaction();
			String email = member.getEmail();
			tx.commit();
			return memberDao.selectByEmail(email);
		} catch (Exception e) {
			tx.rollback();
			e.printStackTrace();
		}
		String email = member.getEmail();
		return memberDao.selectByEmail(email);
	}

}
