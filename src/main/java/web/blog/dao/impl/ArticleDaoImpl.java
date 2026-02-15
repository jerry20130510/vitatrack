package web.blog.dao.impl;

import core.util.HibernateUtil;
import org.hibernate.Session;
import web.blog.dao.ArticleDao;
import web.blog.vo.Article;

import java.sql.Timestamp;
import java.util.List;

public class ArticleDaoImpl implements ArticleDao {

    private Session getSession() {
        return HibernateUtil.getSessionFactory().getCurrentSession();
    }

    @Override
    public List<Article> findAll(int offset, int limit) {
        return getSession()
            .createQuery("FROM Article ORDER BY id DESC", Article.class)
            .setFirstResult(offset)
            .setMaxResults(limit)
            .list();
    }

    @Override
    public List<Article> findByCategory(String category, int offset, int limit) {
        return getSession()
            .createQuery("FROM Article WHERE category = :category ORDER BY id DESC", Article.class)
            .setParameter("category", category)
            .setFirstResult(offset)
            .setMaxResults(limit)
            .list();
    }

    @Override
    public List<Article> findByAuthorSlug(String authorSlug, int offset, int limit) {
        return getSession()
            .createQuery("FROM Article WHERE authorSlug = :authorSlug ORDER BY id DESC", Article.class)
            .setParameter("authorSlug", authorSlug)
            .setFirstResult(offset)
            .setMaxResults(limit)
            .list();
    }

    @Override
    public int countAll() {
        return getSession()
            .createQuery("SELECT COUNT(*) FROM Article", Long.class)
            .uniqueResult()
            .intValue();
    }

    @Override
    public int countByCategory(String category) {
        return getSession()
            .createQuery("SELECT COUNT(*) FROM Article WHERE category = :category", Long.class)
            .setParameter("category", category)
            .uniqueResult()
            .intValue();
    }

    @Override
    public int countByAuthorSlug(String authorSlug) {
        return getSession()
            .createQuery("SELECT COUNT(*) FROM Article WHERE authorSlug = :authorSlug", Long.class)
            .setParameter("authorSlug", authorSlug)
            .uniqueResult()
            .intValue();
    }

    @Override
    public Article findByTitleSlugWithAuthor(String titleSlug) {
        return getSession()
            .createQuery("FROM Article WHERE titleSlug = :titleSlug", Article.class)
            .setParameter("titleSlug", titleSlug)
            .uniqueResult();
    }

    @Override
    public int incrementViews(String titleSlug) {
        return getSession()
            .createQuery("UPDATE Article SET totalViews = totalViews + 1 WHERE titleSlug = :titleSlug")
            .setParameter("titleSlug", titleSlug)
            .executeUpdate();
    }

    @Override
    public int incrementLikes(String titleSlug) {
        return getSession()
            .createQuery("UPDATE Article SET totalLikes = totalLikes + 1 WHERE titleSlug = :titleSlug")
            .setParameter("titleSlug", titleSlug)
            .executeUpdate();
    }

    @Override
    public int incrementShares(String titleSlug) {
        return getSession()
            .createQuery("UPDATE Article SET totalShares = totalShares + 1 WHERE titleSlug = :titleSlug")
            .setParameter("titleSlug", titleSlug)
            .executeUpdate();
    }

    @Override
    public int getTotalViews(String titleSlug) {
        Integer result = getSession()
            .createQuery("SELECT totalViews FROM Article WHERE titleSlug = :titleSlug", Integer.class)
            .setParameter("titleSlug", titleSlug)
            .uniqueResult();
        return result != null ? result : 0;
    }

    @Override
    public int getTotalLikes(String titleSlug) {
        Integer result = getSession()
            .createQuery("SELECT totalLikes FROM Article WHERE titleSlug = :titleSlug", Integer.class)
            .setParameter("titleSlug", titleSlug)
            .uniqueResult();
        return result != null ? result : 0;
    }

    @Override
    public int getTotalShares(String titleSlug) {
        Integer result = getSession()
            .createQuery("SELECT totalShares FROM Article WHERE titleSlug = :titleSlug", Integer.class)
            .setParameter("titleSlug", titleSlug)
            .uniqueResult();
        return result != null ? result : 0;
    }

    @Override
    public Article findById(Long id) {
        return getSession().find(Article.class, id);
    }

    @Override
    public int insert(Article article) {
        getSession().persist(article);
        return 1;
    }

    @Override
    public int update(Article article, Timestamp expectedUpdatedAt) {
        return getSession()
            .createQuery("""
                UPDATE Article SET
                    titleDisplay = :titleDisplay,
                    summary = :summary,
                    content = :content,
                    imageUrl = :imageUrl,
                    category = :category,
                    updatedAt = :newUpdatedAt
                WHERE id = :id AND updatedAt = :expectedUpdatedAt
                """)
            .setParameter("titleDisplay", article.getTitleDisplay())
            .setParameter("summary", article.getSummary())
            .setParameter("content", article.getContent())
            .setParameter("imageUrl", article.getImageUrl())
            .setParameter("category", article.getCategory())
            .setParameter("newUpdatedAt", article.getUpdatedAt())
            .setParameter("id", article.getId())
            .setParameter("expectedUpdatedAt", expectedUpdatedAt)
            .executeUpdate();
    }

    @Override
    public void delete(Article article) {
        getSession().remove(article);
    }
}
