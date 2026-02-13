package web.member.dao.impl;

import org.hibernate.Session;

import core.util.HibernateUtil;
import web.member.dao.MemberDao;
import web.member.vo.Member;

public class MemberDaoImpl implements MemberDao {

	@Override
	public int insert(Member member) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.persist(member);
		return 1;
	}

	@Override
	public int deleteByEmail(String email) {
		final String hql = "DELETE FROM Member m WHERE m.email = :email";
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		int deleteConut = session.createQuery(hql).setParameter("email", email).executeUpdate();
		return deleteConut;
	}

	// 更新單筆會員
	@Override
	public int updateByEmail(Member member) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.merge(member);
		return 1;
	}

	@Override
	public Member selectByEmail(String email) {
		final String hql = "FROM Member m WHERE m.email = :email";
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		return (Member) session.createQuery(hql).setParameter("email", email).uniqueResult();
	}

	@Override
	public Member SelectByEmailandPassword(String email, String password) {
		final String hql = "FROM Member m WHERE m.email = :email AND m.password= :password";

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Member member = session.createQuery(hql, Member.class).setParameter("email", email)
				.setParameter("password", password).uniqueResult();
		return member;

	}

}
