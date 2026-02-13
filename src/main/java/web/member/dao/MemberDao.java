package web.member.dao;

import web.member.vo.Member;

public interface MemberDao {
	
	int insert(Member member);
	
	int deleteByEmail(String Email);
	
	int updateByEmail(Member member);
	
	Member selectByEmail(String email);
	
	Member SelectByEmailandPassword(String email, String password);

}
