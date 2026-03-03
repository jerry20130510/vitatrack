package web.member.dao.impl;

import org.hibernate.Session;

import core.util.HibernateUtil;
import web.member.dao.PasswordResetTokensDao;
import web.member.vo.PasswordResetTokens;

public class PasswordResetTokensDaoImpl implements PasswordResetTokensDao {
	//建立token
	@Override 
	public void insert(PasswordResetTokens token) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.persist(token);
	}
	//查token
	@Override
	public PasswordResetTokens findByToken(String token) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		PasswordResetTokens result = session.createQuery("FROM PasswordResetTokens WHERE token = :token",
				PasswordResetTokens.class)
		.setParameter("token", token)
		.uniqueResult();
		return result;
	}
    //存已用過的token進去資料庫
	@Override
	public void update(PasswordResetTokens token) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		session.update(token);
	}
	
}
