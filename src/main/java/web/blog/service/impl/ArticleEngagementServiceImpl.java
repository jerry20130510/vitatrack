package web.blog.service.impl;

import core.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import web.blog.dao.ArticleDao;
import web.blog.dao.impl.ArticleDaoImpl;
import web.blog.service.ArticleEngagementService;

public class ArticleEngagementServiceImpl implements ArticleEngagementService {
    private final ArticleDao articleDao = new ArticleDaoImpl();

    @Override
    public String incrementViews(String slug) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            int rows = articleDao.incrementViews(slug);
            tx.commit();
            return rows > 0 ? null : "文章不存在";
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException(e);
        }
    }

    @Override
    public String incrementLikes(String slug) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            int rows = articleDao.incrementLikes(slug);
            tx.commit();
            return rows > 0 ? null : "文章不存在";
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException(e);
        }
    }

    @Override
    public String incrementShares(String slug) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            int rows = articleDao.incrementShares(slug);
            tx.commit();
            return rows > 0 ? null : "文章不存在";
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getLikes(String slug) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            int result = articleDao.getTotalLikes(slug);
            tx.commit();
            return result;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getShares(String slug) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            int result = articleDao.getTotalShares(slug);
            tx.commit();
            return result;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException(e);
        }
    }
}
