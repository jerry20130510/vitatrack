package web.member.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.naming.NamingException;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import core.util.HibernateUtil;
import web.checkout.vo.CartItem;
import web.checkout.vo.Orders;
import web.member.dao.MemberDao;
import web.member.dao.impl.MemberDaoImpl;
import web.member.service.MemberService;
import web.member.vo.Member;
import web.member_admin.dto.PageResultResponse;
import web.product.vo.Product;
import web.member.dto.CartItemResponse;
import web.member.dto.UpdateMemberRequest;

public class MemberServiceImpl implements MemberService {
	private MemberDao memberDao;
	private PasswordEncoder passwordEncoder;

	public MemberServiceImpl() throws NamingException {
		memberDao = new MemberDaoImpl();
		passwordEncoder = new BCryptPasswordEncoder();

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
			// 加密
			String encoded = passwordEncoder.encode(member.getPassword());
			member.setPassword(encoded);
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
			e.printStackTrace();
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
			if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
				return null;
			}
			Member dbMember = memberDao.selectByEmail(email);
			if (dbMember == null) {
				tx.commit();
				return null;
			}
			String dbPassword = dbMember.getPassword();
		    boolean isBcrypt = dbPassword != null && dbPassword.startsWith("$2");
		    if (isBcrypt) {
		    	if (!passwordEncoder.matches(password, dbPassword)) {
	                tx.commit();
	                return null;
	            }else {
	            	if (!password.equals(dbPassword)) {
	                    tx.commit();
	                    return null;
	                }
				}
		    	  String newHash = passwordEncoder.encode(password);
		          dbMember.setPassword(newHash);
		          memberDao.updateByEmail(dbMember);
			}
			

			// 資料庫儲存的雜湊值」不匹配時，判定登入失敗。
			// (使用者輸入的明碼,資料庫中儲存的BCrypt hash)
//			if (!passwordEncoder.matches(password,dbMember.getPassword())) {
//				tx.commit();
//				return null;
//			}
			tx.commit();
			return dbMember;

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
					String encoded = passwordEncoder.encode(dto.getPassword());
					member.setPassword(encoded);
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

	@Override
	public Boolean changePassword(String email, String oldPassword, String newPassword) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			Member memberDb = memberDao.selectByEmail(email);
			if (memberDb == null) {
	            return false;
	        }

			
			  String dbPassword = memberDb.getPassword();
		      boolean isBcrypt = dbPassword != null && dbPassword.startsWith("$2");
		      if (isBcrypt) {
				if (!passwordEncoder.matches(oldPassword, dbPassword)) {
					tx.commit();
	                return false;
				}	
			}else {
				if (!oldPassword.equals(dbPassword)) {
					tx.commit();
	                return false;
				}
			}
			
			String newPasswordHash = passwordEncoder.encode(newPassword);
			memberDb.setPassword(newPasswordHash);
			memberDao.updateByEmail(memberDb); 
			System.out.println("User input oldPassword: " + oldPassword);
			System.out.println("DB stored password hash: " + memberDb.getPassword());
			System.out.println("Password match result: " + passwordEncoder.matches(oldPassword, memberDb.getPassword()));
			tx.commit();
			return true;
		} catch (Exception e) {
			if (tx != null) {
				tx.rollback();
				e.printStackTrace();
			}
			throw new IllegalArgumentException("密碼變更失敗!",e);
		}
	}

	@Override
	public boolean remove(String email) {
		if (email == null || email.trim().isEmpty()) {
			return false;
		}
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			int count = memberDao.deleteByEmail(email);
			tx.commit();
			return count > 0;
		} catch (Exception e) {
			if (tx != null) {
				tx.rollback();
			}
			throw new IllegalArgumentException("刪除失敗", e);
		}
	}

	@Override
	public PageResultResponse<Orders> viewMyOrder(Integer memberId, int page, int size) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			// 取得分頁
			int offset = (page - 1) * size;
			// 當頁訂單資料
			List<Orders> orders = memberDao.selectAllOrdersWithPagination(memberId, offset, size);
			// 總訂單數
			long totalOrders = memberDao.countAllOrdersById(memberId);
			// 總頁數
			int totalPages = (int) Math.ceil((float) totalOrders / size);
			tx.commit();
			return new PageResultResponse<>(orders, totalOrders, totalPages, page);

		} catch (Exception e) {
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
			throw new IllegalArgumentException("取得訂單資料失敗", e);
		}
	}

	@Override
	public List<CartItemResponse> viewMyCartItem(Member member) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			List<Object[]> items = memberDao.selectAllCartItems(member.getMemberId());
			List<CartItemResponse> result = items.stream().map(obj -> {
				CartItem cartItem = (CartItem) obj[0];
				Product product = (Product) obj[1];
				return new CartItemResponse(cartItem, product);
			}).collect(Collectors.toList());
			tx.commit();
			return result;
		} catch (Exception e) {
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
			throw new IllegalArgumentException("取得購物車資料失敗", e);
		}

	}

}
