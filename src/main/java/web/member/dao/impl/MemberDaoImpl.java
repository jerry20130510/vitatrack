package web.member.dao.impl;


import org.hibernate.Session;

import core.util.HibernateUtil;
import web.member.dao.MemberDao;
import web.member.vo.Member;

public class MemberDaoImpl implements MemberDao {
//	private DataSource ds;
//
//	public MemberDaoImpl() {
//		try {
//			ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/vitatrack");
//		} catch (NamingException e) {
//			e.printStackTrace();
//		}
//	}

	@Override
	public int insert(Member member) {
		 Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		    session.persist(member);
		    return 1;

	}

	@Override
	public int deleteById(Integer memberId) {
		final String hql = "DELETE FROM Member m WHERE m.memberId = :id";
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		int deleteConut = session.createQuery(hql).setParameter("id", memberId).executeUpdate();
		return deleteConut;
	}

	// 更新 單筆會員
	@Override
	public int update(Member member) {
		final String hql = "UPDATE Member m " + "SET m.name = :name, " + "m.password = :password, "
				+ "m.address = :address, " + "m.phone = :phone " + "WHERE m.memberId = :id";
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		return session.createQuery(hql).setParameter("name", member.getName())
				.setParameter("password", member.getPassword()).setParameter("address", member.getAddress())
				.setParameter("phone", member.getPhone()).setParameter("id", member.getMemberId()).executeUpdate();
	}

	@Override
	public Member selectByEmail(String email) {
		final String hql = "FROM Member m WHERE m.email = :email";
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		return (Member) session.createQuery(hql)
				.setParameter("email", email)
				.uniqueResult();
	}

	@Override
	public Member SelectByEmailandPassword(String email, String password) {
		final String hql = "FROM Member m WHERE m.email = :email AND m.password= :password";

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		Member member = session.createQuery(hql, Member.class)
				.setParameter("email", email)
				.setParameter("password", password)
				.uniqueResult();
		return member;

	}

}
