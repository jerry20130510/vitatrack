package web.member.vo;

import java.sql.Timestamp;

public class Member {

	private Integer memberId;
	private String name;
	private String email;
	private String phone;
	private String address;
	private String password;
	private String confirmPassword;
	private String verifyCode;
	private Integer memberStatus;
	private Timestamp registrationTime;

	public Member() {

	}

	public Member(Integer memberId, String name, String email, String phone, String address, String password,
			String confirmPassword, String verifyCode, Integer memberStatus, Timestamp registrationTime) {
		super();
		this.memberId = memberId;
		this.name = name;
		this.email = email;
		this.phone = phone;
		this.address = address;
		this.password = password;
		this.confirmPassword = confirmPassword;
		this.verifyCode = verifyCode;
		this.memberStatus = memberStatus;
		this.registrationTime = registrationTime;
	}

	public Integer getMemberId() {
		return memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public String getVerifyCode() {
		return verifyCode;
	}

	public void setVerifyCode(String verifyCode) {
		this.verifyCode = verifyCode;
	}

	public Integer getMemberStatus() {
		return memberStatus;
	}

	public void setMemberStatus(Integer memberStatus) {
		this.memberStatus = memberStatus;
	}

	public Timestamp getRegistrationTime() {
		return registrationTime;
	}

	public void setRegistrationTime(Timestamp registrationTime) {
		this.registrationTime = registrationTime;
	}

	

}
