package web.blog.service.impl;

import core.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import web.blog.dao.ArticleDao;
import web.blog.dao.impl.ArticleDaoImpl;
import web.blog.service.ArticleService;
import web.blog.vo.Article;
import web.blog.vo.ArticleListResponse;

import java.util.List;

public class ArticleServiceImpl implements ArticleService {

    private final ArticleDao articleDao = new ArticleDaoImpl();

    @Override
    public ArticleListResponse listArticles(int page, int size, String category, String authorSlug) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            List<Article> articles;
            int totalElements;
            int offset = page * size;
            
            if (category != null) {
                articles = articleDao.findByCategory(category, offset, size);
                totalElements = articleDao.countByCategory(category);
            } else if (authorSlug != null) {
                articles = articleDao.findByAuthorSlug(authorSlug, offset, size);
                totalElements = articleDao.countByAuthorSlug(authorSlug);
            } else {
                articles = articleDao.findAll(offset, size);
                totalElements = articleDao.countAll();
            }
            
            int totalPages = (int) Math.ceil((double) totalElements / size);
            tx.commit();
            return new ArticleListResponse(page, size, totalElements, totalPages, articles);
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException(e);
        }
    }

    @Override
    public Article getArticleDetail(String titleSlug) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Article article = articleDao.findByTitleSlugWithAuthor(titleSlug);
            tx.commit();
            return article;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException(e);
        }
    }

    @Override
    public String incrementViews(String titleSlug) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            int rows = articleDao.incrementViews(titleSlug);
            tx.commit();
            return rows > 0 ? null : "文章不存在";
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException(e);
        }
    }

    @Override
    public String incrementLikes(String titleSlug) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            int rows = articleDao.incrementLikes(titleSlug);
            tx.commit();
            return rows > 0 ? null : "文章不存在";
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException(e);
        }
    }

    @Override
    public String incrementShares(String titleSlug) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            int rows = articleDao.incrementShares(titleSlug);
            tx.commit();
            return rows > 0 ? null : "文章不存在";
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getViews(String titleSlug) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            int result = articleDao.getTotalViews(titleSlug);
            tx.commit();
            return result;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getLikes(String titleSlug) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            int result = articleDao.getTotalLikes(titleSlug);
            tx.commit();
            return result;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getShares(String titleSlug) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            int result = articleDao.getTotalShares(titleSlug);
            tx.commit();
            return result;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException(e);
        }
    }
}
