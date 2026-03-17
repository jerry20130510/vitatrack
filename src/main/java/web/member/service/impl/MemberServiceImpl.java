package web.member.service.impl;

import java.util.List;

import java.util.stream.Collectors;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import web.checkout.vo.CartItem;
import web.checkout.vo.Orders;
import web.member.dao.MemberDao;

import web.member.service.MemberService;
import web.member.vo.Member;
import web.member_admin.dto.PageResultResponse;
import web.product.vo.Product;
import web.member.dto.CartItemResponse;
import web.member.dto.UpdateMemberRequest;

@Service
public class MemberServiceImpl implements MemberService {
	@Autowired
	private MemberDao memberDao;
	@Autowired
	private PasswordEncoder passwordEncoder;

	
	@Transactional
	@Override	
	public String register(Member member) {
			//基礎驗證
			validateName(member.getName());
			validateEmail(member.getEmail());
			validatePhone(member.getPhone());
			validatePassword(member.getPassword(), member.getConfirmPassword());
			
			if (memberDao.selectByEmail(member.getEmail()) != null) {
				throw new IllegalArgumentException("此帳號已經被註冊了");
			}
			member.setAddress(validateAddress(member.getAddress()));
			String encoded = passwordEncoder.encode(member.getPassword());
			member.setPassword(encoded);
			//註冊方法在通過所有驗證後，呼叫 memberDao.insert(member)，新增資料。
			memberDao.insert(member);

			//全部成功，回傳 null 代表沒有錯誤訊息或 代表的是錯誤訊息為空」。
			return null;
	}

	@Transactional
	@Override
	public Member login(Member member) {

		// 驗證帳號和密碼
		try {

			String email = member.getEmail();
			String password = member.getPassword();
			if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
				return null;
			}
			Member dbMember = memberDao.selectByEmail(email);
			if (dbMember == null) {
				return null;
			}
			String dbPassword = dbMember.getPassword();
			// 資料庫儲存的雜湊值」不匹配時，判定登入失敗。
						            //(使用者輸入的明碼,資料庫中儲存的BCrypt hash)
			if (!passwordEncoder.matches(password, dbPassword)) {
				return null;
			}
			String newHash = passwordEncoder.encode(password);
			dbMember.setPassword(newHash);
			memberDao.updateByEmail(dbMember);
			return dbMember;

		} catch (Exception e) {
			throw new IllegalArgumentException("系統錯誤，登入失敗!", e);
		}
	}

	@Transactional
	@Override
	public Member profile(Member member) {

		// 顯示當前會員資料
		try {

			String email = member.getEmail();
			Member dbEmail = memberDao.selectByEmail(email);
			return dbEmail;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Transactional
	@Override
	public Member updateProfile(String email, UpdateMemberRequest dto) {
		// 更新會員資料
		try {
			// 1:撈出舊資料
			Member member = memberDao.selectByEmail(email);
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

			return member;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Transactional
	@Override
	public Boolean changePassword(String email, String oldPassword, String newPassword) {

		try {
			Member memberDb = memberDao.selectByEmail(email);
			if (memberDb == null) {
				return false;
			}
			String dbPassword = memberDb.getPassword();
			boolean isBcrypt = dbPassword != null && dbPassword.startsWith("$2");
			if (isBcrypt) {
				if (!passwordEncoder.matches(oldPassword, dbPassword)) {
					return false;
				}
			} else {
				if (!oldPassword.equals(dbPassword)) {
					return false;
				}
			}

			String newPasswordHash = passwordEncoder.encode(newPassword);
			memberDb.setPassword(newPasswordHash);
			memberDao.updateByEmail(memberDb);
			System.out.println("User input oldPassword: " + oldPassword);
			System.out.println("DB stored password hash: " + memberDb.getPassword());
			System.out
					.println("Password match result: " + passwordEncoder.matches(oldPassword, memberDb.getPassword()));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException("密碼變更失敗!", e);
		}
	}

	@Transactional
	@Override
	public boolean remove(String email) {
		if (email == null || email.trim().isEmpty()) {
			return false;
		}

		try {
			int count = memberDao.deleteByEmail(email);
			return count > 0;
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException("刪除失敗", e);
		}
	}

	@Transactional
	@Override
	public PageResultResponse<Orders> viewMyOrder(Integer memberId, int page, int size) {

		try {
			// 取得分頁
			int offset = (page - 1) * size;
			// 當頁訂單資料
			List<Orders> orders = memberDao.selectAllOrdersWithPagination(memberId, offset, size);
			// 總訂單數
			long totalOrders = memberDao.countAllOrdersById(memberId);
			// 總頁數
			int totalPages = (int) Math.ceil((float) totalOrders / size);
			return new PageResultResponse<>(orders, totalOrders, totalPages, page);

		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException("取得訂單資料失敗", e);
		}
	}

	@Transactional
	@Override
	public List<CartItemResponse> viewMyCartItem(Member member) {

		try {

			List<Object[]> items = memberDao.selectAllCartItems(member.getMemberId());
			List<CartItemResponse> result = items.stream().map(obj -> {
				CartItem cartItem = (CartItem) obj[0];
				Product product = (Product) obj[1];
				return new CartItemResponse(cartItem, product);
			}).collect(Collectors.toList());
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException("取得購物車資料失敗", e);
		}

	}

}
