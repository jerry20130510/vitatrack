package web.member_admin.service.impl;

import java.util.List;

import javax.naming.NamingException;

import org.hibernate.Session;
import org.hibernate.Transaction;
import core.util.HibernateUtil;
import web.member.dao.MemberDao;
import web.member.dao.impl.MemberDaoImpl;

import web.member_admin.dto.MemberListResponse;
import web.member_admin.dto.PageResultResponse;
import web.member_admin.service.MemberAdminService;

public class MemberAdminServiceImpl implements MemberAdminService {
	private MemberDao memberDao;

	public MemberAdminServiceImpl() throws NamingException {
		memberDao = new MemberDaoImpl();
	}

	@Override
	public PageResultResponse<MemberListResponse> getMemberInfo(int page, int size) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			// 取得分頁
			int offset = (page - 1) * size;
			List<MemberListResponse> members =  memberDao.selectAllWithPagination(offset, size);
			// 計算總會員數
			long totalNumber = memberDao.countAllMembers();
			// 計算共需要幾頁
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

}
