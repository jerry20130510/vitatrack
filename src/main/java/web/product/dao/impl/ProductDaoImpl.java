package web.product.dao.impl;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

<<<<<<< HEAD
import org.hibernate.Session;

=======
import core.util.HibernateUtil;
>>>>>>> main
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
<<<<<<< HEAD
		Tra
		Session session = Session.
		
		String sql = "INSERT INTO product "
				+ "(sku, category_id, product_name, size, price, stock_quantity, status, short_description, description, created_by_admin_id) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try (Connection conn = ds.getConnection(); 
				PreparedStatement ps = conn.prepareStatement(sql);
				
		){
			ps.setString(1, product.getSku());
			ps.setLong(2, product.getCategoryId());
			ps.setString(3, product.getProductName());
			ps.setString(4, product.getSize());
			ps.setInt(5, product.getPrice());
			ps.setInt(6, product.getStockQuantity());
			ps.setInt(7, product.getStatus());
			ps.setString(8, product.getShortDescription());
			ps.setString(9, product.getDescription());
			ps.setLong(10, product.getCreatedByAdminId());

			int affected = ps.executeUpdate();
			return affected == 1;

		} catch (SQLException e) {
			e.printStackTrace();
=======
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
		Session session = sessionFactory.getCurrentSession();
		try {
			Transaction transaction = session.beginTransaction();
			session.persist(product);
			transaction.commit();
			return true;
		} catch (HibernateException e) {
			session.getTransaction().rollback();
			
>>>>>>> main
		}
		return false;
	}

}
