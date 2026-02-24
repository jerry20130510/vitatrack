package web.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data 
@AllArgsConstructor
@NoArgsConstructor
public class ForgetPasswordRequest {
	private String email;
}
