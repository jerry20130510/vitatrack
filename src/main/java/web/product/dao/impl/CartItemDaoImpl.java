package web.product.dao.impl;

import org.hibernate.Session;
import org.hibernate.Transaction;

import core.util.HibernateUtil;
import web.product.dao.CartItemDao;
import web.product.vo.CartItem;

public class CartItemDaoImpl implements CartItemDao {

    @Override
    public int insert(CartItem cartItem) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            session.save(cartItem);
            tx.commit();
            return 1;
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public CartItem selectByMemberIdAndSku(Integer memberId, String sku) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            CartItem result = session.createQuery(
                    "FROM ProductCartItem WHERE memberId = :memberId AND sku = :sku", CartItem.class)
                    .setParameter("memberId", memberId)
                    .setParameter("sku", sku)
                    .uniqueResult();

            tx.commit();
            return result;
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int update(CartItem cartItem) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        try {
            session.update(cartItem);
            tx.commit();
            return 1;
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
            return -1;
        }
    }
}