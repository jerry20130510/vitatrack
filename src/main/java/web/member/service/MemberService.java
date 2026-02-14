package web.member.service;

import web.member.vo.Member;
import web.member.vo.UpdateMemberRequest;

public interface MemberService {

	default void validateName(String name) {
		if (name == null || name.trim().isEmpty()) {
			throw new IllegalArgumentException("會員名為必填欄位!");
		}
	}

	default void validateEmail(String email) {
		if (email == null || !email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
			throw new IllegalArgumentException("email格式錯誤或未填寫!");
		}
	}

	default void validatePhone(String phone) {
		if (phone == null || !phone.matches("^09[0-9]{8}$")) {
			throw new IllegalArgumentException("手機號碼格式錯誤或未填寫!");
		}
	}

	default void validatePassword(String password, String confirmPassword) {
		if (password == null || !password.matches("^(?=.*[A-Za-z])(?=.*\\d).{8,}$")) {
			throw new IllegalArgumentException("密碼格式錯誤或未填寫!");
		}
		if (!password.equals(confirmPassword)) {
			throw new IllegalArgumentException("與設定密碼不一致，請重新輸入!");
		}
	}

	default String validateAddress(String address) {
	        if (address == null) return null;
	        address = address.trim();
	        if (address.isEmpty()) return null;
	        if (address.length() > 200) throw new IllegalArgumentException("地址長度過長");
	        // 簡單防 XSS
	        return address.replaceAll("<[^>]*>", "");
	    }

	String register(Member member);

	Member login(Member member);

	Member profile(Member member);

    Member updateProfile(Integer memberID,UpdateMemberRequest dto);

}
