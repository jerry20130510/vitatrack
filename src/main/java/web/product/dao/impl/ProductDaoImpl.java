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
	
	 private final SessionFactory sessionFactory;

	 public ProductDaoImpl() {
		 this.sessionFactory = HibernateUtil.getSessionFactory();
		 }

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

	@Override
	public boolean updateEditableFields(Product product) {
		Session session = sessionFactory.getCurrentSession();

        // 先用 sku 找到「資料庫那筆」
        Product dbProduct = session.get(Product.class, product.getSku());
        if (dbProduct == null) {
            return false;
        }
        dbProduct.setProductName(product.getProductName());
        dbProduct.setPrice(product.getPrice());
        dbProduct.setSize(product.getSize());
        dbProduct.setStockQuantity(product.getStockQuantity());
        dbProduct.setDescription(product.getDescription());
        dbProduct.setShortDescription(product.getShortDescription());
		if (product.getStatus() != null) {
			dbProduct.setStatus(product.getStatus());
		}
        dbProduct.setUpdatedByAdminId(1L);
        return true;
    }

	@Override
	public boolean deleteBySku(String sku) {
		Session session = sessionFactory.getCurrentSession();
		Product target = session.get(Product.class, sku);
		if (target == null) {
			return false;
		}
		session.delete(target);
		return true;
	}

	@Override
	public Product selectBySku(String sku) {
	    SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
	    Session session = sessionFactory.getCurrentSession();
	    try {
	        Transaction transaction = session.beginTransaction();

	        Product product = session.get(Product.class, sku);

	        transaction.commit();
	        return product;
	    } catch (Exception e) {
	        session.getTransaction().rollback();
	        throw e;
	    }
		
	}

}
