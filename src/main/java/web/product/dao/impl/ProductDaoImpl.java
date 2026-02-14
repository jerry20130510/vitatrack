package web.product.dao.impl;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import core.util.HibernateUtil;
import web.product.dao.ProductDao;
import web.product.vo.Product;

public class ProductDaoImpl implements ProductDao {

	@Override
	public List<Product> selectAll() {
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
		Session session = sessionFactory.getCurrentSession();
		try {
			Transaction transaction = session.beginTransaction();
			
			List<Product> list = session
					.createQuery("FROM Product", Product.class)
					.getResultList();
			transaction.commit();
			return list;
		} catch (Exception e) {
			session.getTransaction().rollback();
			throw e;
		}
	}
	
	@Override
	public boolean insert(Product product) {
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
		Session session = sessionFactory.getCurrentSession();
		try {
			Transaction transaction = session.beginTransaction();
			session.persist(product);
			transaction.commit();
			return true;
		} catch (HibernateException e) {
			session.getTransaction().rollback();
			
		}
		return false;
	}

}
