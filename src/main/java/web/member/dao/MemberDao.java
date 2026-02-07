package web.member.dao;

import web.member.vo.Member;

public interface MemberDao {
	
	
	int insert(Member member);
	
	int deleteById(Integer id);
	
	int update(Member member);
	
	Member selectByEmail(String email);
	
	Member SelectByEmailandPassword(String email, String password);

}
