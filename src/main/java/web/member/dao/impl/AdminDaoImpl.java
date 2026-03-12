package web.member.dao.impl;



import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import web.member.dao.AdminDao;

import web.member.vo.Admin;


@Repository
public class AdminDaoImpl implements AdminDao {
	
	@PersistenceContext
	private Session session;
	
	

	@Override
	public Admin SelectByAccountandPassword(String account, String password) {
		final String hql = "FROM Admin a WHERE a.account = :account AND a.password= :password";

		Admin admin = session.createQuery(hql, Admin.class)
				.setParameter("account", account)
				.setParameter("password", password).uniqueResult();
		return admin;
	}


}
