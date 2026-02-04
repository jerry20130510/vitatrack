package web.member.service;

import web.member.vo.Member;

public interface MemberService {

	String register(Member member);
	
	Member login(Member member);

}
