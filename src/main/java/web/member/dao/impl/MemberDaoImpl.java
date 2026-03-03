package web.member.dao.impl;

import java.util.List;

import org.hibernate.Session;

import core.util.HibernateUtil;
import web.member.dao.MemberDao;
import web.member.vo.Member;
import web.member_admin.dto.MemberListResponse;

public class MemberDaoImpl implements MemberDao {

	private Session getSession() {
		return HibernateUtil.getSessionFactory().getCurrentSession();
	}

	@Override
	public int insert(Member member) {

		getSession().persist(member);
		return 1;
	}

	@Override
	public int deleteByEmail(String email) {
		final String hql = "DELETE FROM Member m WHERE m.email = :email";

		return getSession().createQuery(hql).setParameter("email", email).executeUpdate();

	}

	// 更新單筆會員
	@Override
	public int updateByEmail(Member member) {

		getSession().merge(member);
		return 1;
	}

	@Override
	public Member selectByEmail(String email) {
		final String hql = "FROM Member m WHERE m.email = :email";

		return (Member) getSession().createQuery(hql).setParameter("email", email).uniqueResult();
	}

	@Override
	public Member SelectByEmailandPassword(String email, String password) {
		final String hql = "FROM Member m WHERE m.email = :email AND m.password= :password";

		Member member = getSession().createQuery(hql, Member.class).setParameter("email", email)
				.setParameter("password", password).uniqueResult();
		return member;

	}

	@Override
	public List<MemberListResponse> selectAllWithPagination(int offset, int size) {
		return getSession().createQuery(

				"SELECT new web.member_admin.dto.MemberListResponse("
						+ "m.memberId, m.name, m.email, m.phone, m.address, m.memberStatus, m.registrationTime) "
						+ "FROM Member m " 
						+ "ORDER BY m.memberId ASC",
				MemberListResponse.class)
				.setFirstResult(offset)
				.setMaxResults(size)
				.getResultList();

	}

	@Override
	public long countAllMembers() {
		
		 return getSession().createQuery(
		        "SELECT COUNT(m) FROM Member m", 
		        Long.class)
		        .getSingleResult();
	}

	@Override
	public List<MemberListResponse> searchMemberWithPagination(String keyword, int offset, int size) {
		
		 return getSession().createQuery(

				"SELECT new web.member_admin.dto.MemberListResponse("
						+ "m.memberId, m.name, m.email, m.phone, m.address, m.memberStatus, m.registrationTime) "
						+ "FROM Member m "
						+ "WHERE m.name LIKE :keyword " 
						+ "OR m.phone LIKE :keyword "
						+ "OR m.address LIKE :keyword "
						+ "ORDER BY m.memberId DESC ",
				MemberListResponse.class)
				.setParameter("keyword", "%" + keyword + "%")
				.setFirstResult(offset)
				.setMaxResults(size)
				.getResultList();

	}

	@Override
	public long countMemberByKeyword(String keyword) {
		
		 return getSession().createQuery(
		        "SELECT COUNT(m) FROM Member m "
				+ "WHERE m.name LIKE :keyword " 
				+ "OR m.phone LIKE :keyword "
				+ "OR m.address LIKE :keyword " ,
		        Long.class)
				 .setParameter("keyword", "%" + keyword + "%")
				 .getSingleResult();
	}

}
