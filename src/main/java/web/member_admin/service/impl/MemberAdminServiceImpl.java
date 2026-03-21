package web.member_admin.service.impl;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import core.util.HibernateUtil;
import web.member.dao.AdminDao;
import web.member.dao.MemberDao;
import web.member.dto.EditMemberStatusRequest;
import web.member.vo.Admin;
import web.member_admin.dto.MemberListResponse;
import web.member_admin.dto.PageResultResponse;
import web.member_admin.service.MemberAdminService;

@Service
public class MemberAdminServiceImpl implements MemberAdminService {
	@Autowired
	private MemberDao memberDao;
	@Autowired
	private AdminDao adminDao;
	
	@Transactional
	@Override
	public PageResultResponse<MemberListResponse> getMemberInfo(int page, int size) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			//計算分頁起始位置。
			int offset = (page - 1) * size;
			List<MemberListResponse> members =  memberDao.selectAllWithPagination(offset, size);
			//計算總會員數
			long totalNumber = memberDao.countAllMembers();
			//計算共需要幾頁
			int totalPages = (int) Math.ceil((double) totalNumber / size);
			tx.commit();
			return new PageResultResponse<>(members, totalNumber, totalPages, page);

		} catch (Exception e) {
			if (tx != null) {
				tx.rollback();
			}
			throw new IllegalArgumentException("取得會員分頁失敗", e);
		}

	}

	@Transactional
	@Override
	public PageResultResponse<MemberListResponse> searchMemberInfo(String keyword, int page, int size) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Transaction tx = null;
		 if (keyword == null || keyword.trim().isEmpty()) {
		        return getMemberInfo(page, size);
		    } 
		try {
			tx = session.beginTransaction();
			// 取得分頁
			int offset = (page - 1) * size;
			List<MemberListResponse> members = memberDao.searchMemberWithPagination(keyword, offset, size);
			// 計算總會員數
			long totalNumber = memberDao.countMemberByKeyword(keyword);
			// 計算總頁數
			int totalPages = (int) Math.ceil((double) totalNumber / size);
			tx.commit();
			return new PageResultResponse<>(members, totalNumber, totalPages, page);
		} catch (Exception e) {
			if (tx != null) {
				tx.rollback();
			}
			throw new IllegalArgumentException("查詢會員分頁失敗", e);
		}

	}

	@Transactional
	@Override
	public Admin login(Admin admin) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Transaction tx = null;	
		try {
			tx =session.beginTransaction();
		
			String account = admin.getAccount();
			String password = admin.getPassword();
			if (account == null || account.isEmpty()||password == null || password.isEmpty()) {
				return null;
			}
			
			Admin dbAdmin= adminDao.SelectByAccountandPassword(account, password);
			
			if (dbAdmin == null) {
				tx.commit();
				return null;
			}	
			if (!dbAdmin.getPassword().equals(password)) {
				tx.commit();
				return null;
			}
			tx.commit();
			return dbAdmin;
		} catch (Exception e) {
			if (tx!= null) {
				tx.rollback();
			}
			throw new IllegalArgumentException("系統錯誤，登入失敗!", e);
		}
	}

	@Transactional
	@Override
	public EditMemberStatusRequest editMemberStatus(EditMemberStatusRequest member) {
		
		if (member == null || member.getEmail() == null || member.getMemberStatus() == null) {
	        throw new IllegalArgumentException("更新失敗：Email 或會員狀態不能為空！");
	    }
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Transaction tx = null;	
		try {
			tx =session.beginTransaction();
			Integer count= memberDao.updateStatusByEmail(member.getMemberStatus(),member.getEmail());
			if (count == 0) {
				tx.commit();
				return null;
			}
			tx.commit();
			return new EditMemberStatusRequest(member.getEmail(),member.getMemberStatus());
		} catch (Exception e) {
			if (tx != null) {
				tx.rollback();
			}
			e.printStackTrace();
			throw new IllegalArgumentException("系統錯誤，會員狀態更新失敗!", e);
		}
	}

}
