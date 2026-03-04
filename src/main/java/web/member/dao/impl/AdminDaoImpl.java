package web.member.dao.impl;



import org.hibernate.Session;

import core.util.HibernateUtil;
import web.member.dao.AdminDao;

import web.member.vo.Admin;


public class AdminDaoImpl implements AdminDao {

	private Session getSession() {
		return HibernateUtil.getSessionFactory().getCurrentSession();
	}


	@Override
	public Admin SelectByAccountandPassword(String account, String password) {
		final String hql = "FROM Admin a WHERE a.account = :account AND a.password= :password";

		Admin admin = getSession().createQuery(hql, Admin.class)
				.setParameter("account", account)
				.setParameter("password", password).uniqueResult();
		return admin;
	}


}
