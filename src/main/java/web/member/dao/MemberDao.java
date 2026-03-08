package web.member.dao;


import java.util.List;

import web.checkout.vo.Orders;
import web.member.vo.Member;
import web.member_admin.dto.MemberListResponse;

public interface MemberDao {

	int insert(Member member);

	int deleteByEmail(String Email);

	int updateByEmail(Member member);

	Member selectByEmail(String email);

	Member selectByEmailandPassword(String email, String password);
	
	List<MemberListResponse> selectAllWithPagination(int offset,int size);
	
	long countAllMembers();
	
	List<MemberListResponse> searchMemberWithPagination(String keyword, int offset, int size);
	
	long countMemberByKeyword(String keyword);
	
	List<Orders> selectAllOrdersWithPagination(Integer memberId,int offset, int size);
	
	long countAllOrdersById(Integer memberId);
	
}
