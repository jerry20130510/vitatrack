package web.member_admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data  
@NoArgsConstructor
@AllArgsConstructor
public class MemberListResponse {
	
	private Integer memberId;
	private String name;	
	private String email;	
	private String phone;	
	private String address;
	private Integer memberStatus;
	private java.util.Date registrationTime;

	
}
