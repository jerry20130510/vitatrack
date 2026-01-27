package web.member.vo;

import java.sql.Timestamp;

public class Member {
	
	private Integer member_id;
	private String name;
	private String address;
	private String email;
	private String password;
	private String verify_code;
	private Integer member_status;
	private Timestamp registration_time;
	
	public Member() {
		
	}

	public Member(Integer member_id, String name, String address, String email, String password, String verify_code,
			Integer member_status, Timestamp registration_time) {
		super();
		this.member_id = member_id;
		this.name = name;
		this.address = address;
		this.email = email;
		this.password = password;
		this.verify_code = verify_code;
		this.member_status = member_status;
		this.registration_time = registration_time;
	}

	public Integer getMember_id() {
		return member_id;
	}

	public void setMember_id(Integer member_id) {
		this.member_id = member_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getVerify_code() {
		return verify_code;
	}

	public void setVerify_code(String verify_code) {
		this.verify_code = verify_code;
	}

	public Integer getMember_status() {
		return member_status;
	}

	public void setMember_status(Integer member_status) {
		this.member_status = member_status;
	}

	public Timestamp getRegistration_time() {
		return registration_time;
	}

	public void setRegistration_time(Timestamp registration_time) {
		this.registration_time = registration_time;
	}
	
	
	
	
	
	
	
	
}
