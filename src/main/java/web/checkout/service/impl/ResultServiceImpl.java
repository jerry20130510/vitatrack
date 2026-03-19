package web.checkout.service.impl;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Service;

import core.util.HibernateUtil;
import web.checkout.dao.OrderDao;
import web.checkout.service.ResultService;
import web.checkout.vo.ResultDTO;

//查詢訂單狀態(提供前端判斷付款成功或失敗)
@Service
public class ResultServiceImpl implements ResultService {
	
	private final OrderDao orderDao;
	
	public ResultServiceImpl(OrderDao orderDao) {
		this.orderDao = orderDao;
	}

	@Override
	public ResultDTO getOrder(int orderId) {
		
		Transaction tx = null;
		
		try  (Session session = HibernateUtil.getSessionFactory().openSession()){
			
			tx = session.beginTransaction();
			
			ResultDTO raw = orderDao.selectByOrderId(session, orderId);
			
			tx.commit();
			
			return raw;
			
		} catch (Exception e) {
			 if (tx != null) tx.rollback();
		        throw e;
		}
	}

}