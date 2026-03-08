
package web.member_admin.service;


import web.member.vo.Admin;
import web.member_admin.dto.MemberListResponse;
import web.member_admin.dto.PageResultResponse;


public interface MemberAdminService {

	PageResultResponse<MemberListResponse> getMemberInfo(int page, int size);
    
	PageResultResponse<MemberListResponse> searchMemberInfo( String keyword, int page, int size);
	
	Admin login(Admin admin);
}
