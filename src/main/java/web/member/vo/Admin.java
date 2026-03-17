package web.member.vo;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "admin")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Admin {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "admin_id")
	private Integer adminId;
	@Column(name = "account")
	private String account;
	@Column(name = "password")
	private String password;
	@Column(name = "login_time")
	private Timestamp loginTime;
	@Column(name = "last_login_at")
	private Timestamp lastLoginAt;
	@Column(name = "created_at",insertable = false, updatable = false)
	private Timestamp createdAt;
	@Column(name = "updated_at",insertable = false)
	private Timestamp updatedAt;
	@Column(name = "admin_status",insertable = false)
	private Integer adminStatus;
	
}
