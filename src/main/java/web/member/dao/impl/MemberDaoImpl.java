package web.member.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import web.member.dao.MemberDao;
import web.member.vo.Member;


public class MemberDaoImpl implements MemberDao {
	private DataSource ds;

	public MemberDaoImpl() {
		try {
			ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/vitatrack");
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int insert(Member member) {
		String sql = "Insert into member (name, email, phone, address, password)  values(?,?,?,?,?)";
		try (
			 Connection conn = ds.getConnection(); 
			 PreparedStatement pstmt = conn.prepareStatement(sql);
				
			) {
			pstmt.setString(1, member.getName());
			pstmt.setString(2, member.getEmail());
			pstmt.setString(3, member.getPhone());
			pstmt.setString(4, member.getAddress());
			pstmt.setString(5, member.getPassword());
			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	@Override
	public Member selectByEmail(String email) {
		String sql = "select * from member where email =?";
		try (
			Connection conn = ds.getConnection(); 
			PreparedStatement pstmt = conn.prepareStatement(sql);
		) {
			pstmt.setString(1, email);
			try (ResultSet rs = pstmt.executeQuery();) {
				if (rs.next()) {
					Member member = new Member();
					member.setMemberId(rs.getInt("memberId"));
					member.setName(rs.getString("name"));
					member.setEmail(rs.getString("email"));
					member.setPhone(rs.getString("phone"));
					member.setAddress(rs.getString("address"));
					member.setPassword(rs.getString("password"));
					member.setVerifyCode(rs.getString("verifyCode"));
					member.setMemberStatus(rs.getInt("memberStatus"));
					member.setRegistrationTime(rs.getTimestamp("registrationTime"));
					return member;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Member SelectByEmailandPassword(String email, String password) {
		String sql = "select * from member where email =? and password=?";
		try (
			Connection conn = ds.getConnection(); 
			PreparedStatement pstmt = conn.prepareStatement(sql);
		) {
			pstmt.setString(1, email);
			pstmt.setString(2, password);
			try (ResultSet rs = pstmt.executeQuery();) {
				if (rs.next()) {
					Member member = new Member();
					member.setMemberId(rs.getInt("member_id"));
					member.setName(rs.getString("name"));
					member.setEmail(rs.getString("email"));
					member.setPhone(rs.getString("phone"));
					member.setAddress(rs.getString("address"));
					member.setPassword(rs.getString("password"));
					member.setVerifyCode(rs.getString("verify_code"));
					member.setMemberStatus(rs.getInt("member_status"));
					member.setRegistrationTime(rs.getTimestamp("registration_time"));
					return member;
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
