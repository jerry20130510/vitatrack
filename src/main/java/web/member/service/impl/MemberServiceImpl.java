package web.member.service.impl;

import javax.naming.NamingException;

import org.hibernate.Session;
import org.hibernate.Transaction;

import core.util.HibernateUtil;
import web.member.dao.MemberDao;
import web.member.dao.impl.MemberDaoImpl;
import web.member.service.MemberService;
import web.member.vo.Member;
import web.member.vo.UpdateMemberRequest;

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
			tx = session.beginTransaction();
			// 1 姓名不能空白
			validateName(member.getName());
			// 2 電子郵件 必須為^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$
			validateEmail(member.getEmail());
			// 3 手機 必須是09開頭且共10位數字("^09[0-9]{8}$")
			validatePhone(member.getPhone());
			// 4處理地址 (因為是非必填，先處理空值)
			member.setAddress(validateAddress(member.getAddress()));
			// 5 密碼 密碼至少為 8 個字元，且至少包含 1 個英文字母(大小寫皆可)與 1 個數字
			// 6 重新輸入密碼 和密碼 必須一致
			validatePassword(member.getPassword(), member.getConfirmPassword());
			// 7 判斷帳號是否有重複，資料庫的email不能等於新註冊的email
			// 邏輯觀念錯誤 以及 語法回傳值不符。
			// 正確邏輯應該是:檢查資料庫裡「是否已經存在這個會員物件」。如果查出來的結果 不是 null，代表這個Email已經被註冊過了。
			if (memberDao.selectByEmail(member.getEmail()) != null) {
				throw new IllegalArgumentException("此帳號已經被註冊了");
			}
			// 8註冊方法在通過所有驗證後，呼叫 memberDao.insert(member)，新增資料。
			memberDao.insert(member);
			tx.commit();
			// 9全部成功，回傳 null 代表沒有錯誤訊息或 代表的是錯誤訊息為空」。
			return null;
		} catch (Exception e) {
			if (tx != null) {
				tx.rollback();
			}
			throw new IllegalArgumentException("系統錯誤，註冊失敗!", e);
		}
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
			tx.commit();
			return member;
		} catch (Exception e) {
			if (tx != null) {
				tx.rollback();
			}
			throw new IllegalArgumentException("系統錯誤，登入失敗!", e);
		}
	}

	@Override
	public Member profile(Member member) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Transaction tx = null;
		// 顯示當前會員資料
		try {
			tx = session.beginTransaction();
			String email = member.getEmail();
			Member dbEmail = memberDao.selectByEmail(email);
			tx.commit();
			return dbEmail;
		} catch (Exception e) {
			if (tx != null) {
				tx.rollback();
			}
			throw e;
		}
	}

	@Override
	public Member updateProfile(Integer memberId, UpdateMemberRequest dto) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Transaction tx = null;
		// 更新會員資料
		try {
			tx = session.beginTransaction();
			// 1:撈出舊資料
			Member member = session.get(Member.class, memberId);
			if (member == null) {
				throw new IllegalArgumentException("找不到該會員資料");
			}
			// 2:部分更新邏輯，只有傳值進來且不為空時才驗證並更新
			if (dto.getName() != null && !dto.getName().trim().isEmpty()) {
				member.setName(dto.getName());
			}
			if (dto.getAddress() != null) {
				member.setAddress(dto.getAddress());
			}

			if (dto.getPhone() != null && !dto.getPhone().isEmpty()) {
				if (dto.getPhone().matches("^09[0-9]{8}$")) {
					member.setPhone(dto.getPhone());
				} else {
					throw new IllegalArgumentException("手機號碼格式錯誤或未填寫!");
				}
			}
			if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
				if (dto.getPassword().matches("^(?=.*[A-Za-z])(?=.*\\d).{8,}$")) {
					member.setPassword(dto.getPassword());
				} else {
					throw new IllegalArgumentException("密碼格式錯誤或未填寫!");
				}
			}
			tx.commit();
			return member;

		} catch (Exception e) {
			if (tx != null) {
				tx.rollback();
			}
			throw e;
		}
	}

}
