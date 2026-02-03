package web.member.service.impl;

import javax.naming.NamingException;
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
		// 驗證註冊功能中各個必填欄位是否為空，若空return 此欄為必填欄位;前端有驗證的後端程式也要驗證
		// 驗證帳號(即為email)是否註冊過，調用DaoImpl裡的方法進行驗證，若無登入成功;反之登入失敗

		// trim() 方法會回傳一個去除了空格的字串，它永遠不會回傳 null（如果原字串不是 null）。
		// 如果使用者只輸入空白，trim() 會回傳 ""（空字串）。
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
		int count = memberDao.insert(member);
		if (count < 0) {
			return "系統錯誤，註冊失敗!";
		}

		// 9全部成功，回傳 null 代表沒有錯誤訊息或 代表的是 「錯誤訊息為空」。
		return null;
	}

	@Override
	public Member login(Member member) {
		// 驗證帳號和密碼
		String email = member.getEmail();
		String password = member.getPassword();
		if (email == null || email.isEmpty()) {
			return null;
		}
		if (password == null || password.isEmpty()) {
			return null;
		}
		return memberDao.SelectByEmailandPassword(email, password);
	}

}
