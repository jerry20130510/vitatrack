package web.blog.dao.impl;

import core.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;
import web.blog.dao.ArticleDao;
import web.blog.vo.Article;

import java.util.List;

public class ArticleDaoImpl implements ArticleDao {

    private Session getSession() {
        return HibernateUtil.getSessionFactory().getCurrentSession();
    }

    @Override
    public List<Article> findAll(int offset, int limit) {
        Query<Article> query = getSession().createQuery("FROM Article ORDER BY id DESC", Article.class);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.list();
    }

    @Override
    public List<Article> findByCategory(String category, int offset, int limit) {
        Query<Article> query = getSession().createQuery("FROM Article WHERE category = :category ORDER BY id DESC", Article.class);
        query.setParameter("category", category);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.list();
    }

    @Override
    public List<Article> findByAuthorSlug(String authorSlug, int offset, int limit) {
        Query<Article> query = getSession().createQuery("FROM Article WHERE authorSlug = :authorSlug ORDER BY id DESC", Article.class);
        query.setParameter("authorSlug", authorSlug);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.list();
    }

    @Override
    public int countAll() {
        Query<Long> query = getSession().createQuery("SELECT COUNT(*) FROM Article", Long.class);
        return query.uniqueResult().intValue();
    }

    @Override
    public int countByCategory(String category) {
        Query<Long> query = getSession().createQuery("SELECT COUNT(*) FROM Article WHERE category = :category", Long.class);
        query.setParameter("category", category);
        return query.uniqueResult().intValue();
    }

    @Override
    public int countByAuthorSlug(String authorSlug) {
        Query<Long> query = getSession().createQuery("SELECT COUNT(*) FROM Article WHERE authorSlug = :authorSlug", Long.class);
        query.setParameter("authorSlug", authorSlug);
        return query.uniqueResult().intValue();
    }

    @Override
    public Article findByTitleSlugWithAuthor(String titleSlug) {
        Query<Article> query = getSession().createQuery("FROM Article WHERE titleSlug = :titleSlug", Article.class);
        query.setParameter("titleSlug", titleSlug);
        return query.uniqueResult();
    }

    @Override
    public int incrementViews(String titleSlug) {
        Query<?> query = getSession().createQuery("UPDATE Article SET totalViews = totalViews + 1 WHERE titleSlug = :titleSlug");
        query.setParameter("titleSlug", titleSlug);
        return query.executeUpdate();
    }

    @Override
    public int incrementLikes(String titleSlug) {
        Query<?> query = getSession().createQuery("UPDATE Article SET totalLikes = totalLikes + 1 WHERE titleSlug = :titleSlug");
        query.setParameter("titleSlug", titleSlug);
        return query.executeUpdate();
    }

    @Override
    public int incrementShares(String titleSlug) {
        Query<?> query = getSession().createQuery("UPDATE Article SET totalShares = totalShares + 1 WHERE titleSlug = :titleSlug");
        query.setParameter("titleSlug", titleSlug);
        return query.executeUpdate();
    }

    @Override
    public int getTotalViews(String titleSlug) {
        Query<Integer> query = getSession().createQuery("SELECT totalViews FROM Article WHERE titleSlug = :titleSlug", Integer.class);
        query.setParameter("titleSlug", titleSlug);
        Integer result = query.uniqueResult();
        return result != null ? result : 0;
    }

    @Override
    public int getTotalLikes(String titleSlug) {
        Query<Integer> query = getSession().createQuery("SELECT totalLikes FROM Article WHERE titleSlug = :titleSlug", Integer.class);
        query.setParameter("titleSlug", titleSlug);
        Integer result = query.uniqueResult();
        return result != null ? result : 0;
    }

    @Override
    public int getTotalShares(String titleSlug) {
        Query<Integer> query = getSession().createQuery("SELECT totalShares FROM Article WHERE titleSlug = :titleSlug", Integer.class);
        query.setParameter("titleSlug", titleSlug);
        Integer result = query.uniqueResult();
        return result != null ? result : 0;
    }
}
