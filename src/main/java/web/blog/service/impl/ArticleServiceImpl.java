package web.blog.service.impl;

import core.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import web.blog.dao.ArticleDao;
import web.blog.dao.impl.ArticleDaoImpl;
import web.blog.dto.ArticleCreateRequest;
import web.blog.dto.ArticleUpdateRequest;
import web.blog.dto.ArticleListResponse;
import web.blog.vo.Article;
import web.blog.service.ArticleService;
// import web.blog.service.S3PresignedUrlService;

import javax.naming.NamingException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

public class ArticleServiceImpl implements ArticleService {

    private final ArticleDao articleDao = new ArticleDaoImpl();
    // private final S3PresignedUrlService s3Service;

    public ArticleServiceImpl() throws NamingException {
        // this.s3Service = new S3PresignedUrlServiceImpl();
    }

    private void initAuthorFields(Article article) {
        if (article != null && article.getBlogger() != null) {
            article.setAuthorDisplayName(article.getBlogger().getDisplayName());
            article.setAuthorProfileImage(article.getBlogger().getProfileImage());
            article.setBlogger(null);
        }
    }

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
            for (Article a : articles) { initAuthorFields(a);
                
            }
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
            initAuthorFields(article);
            tx.commit();
            return article;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArticleListResponse listArticlesAdmin(String authorSlug, int page, int size) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            int offset = page * size;
            List<Article> articles = articleDao.findByAuthorSlug(authorSlug, offset, size);
            int totalElements = articleDao.countByAuthorSlug(authorSlug);
            int totalPages = (int) Math.ceil((double) totalElements / size);
            for (Article a : articles) {
                initAuthorFields(a);
            }
            tx.commit();
            return new ArticleListResponse(page, size, totalElements, totalPages, articles);
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException(e);
        }
    }

    @Override
    public Article getArticleDetailAdmin(Long id, String authorSlug) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Article article = articleDao.findById(id);
            if (article == null || !article.getAuthorSlug().equals(authorSlug)) {
                tx.commit();
                return null;
            }
            initAuthorFields(article);
            tx.commit();
            return article;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException(e);
        }
    }

    @Override
    public Article createArticle(ArticleCreateRequest request, String authorSlug) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            String titleDisplay = request.getTitleDisplay();
            String summary = request.getSummary();
            String content = request.getContent();
            String category = request.getCategory();
            String imageUrl = request.getImageUrl();

            if (titleDisplay == null || titleDisplay.trim().isEmpty()) {
                tx.commit();
                return null;
            }
            if (summary == null || summary.trim().isEmpty()) {
                tx.commit();
                return null;
            }

            Article article = new Article();
            article.setTitleDisplay(titleDisplay);
            article.setSummary(summary);
            article.setContent(content);
            article.setCategory(category);
            article.setImageUrl(imageUrl); // Direct assignment instead of s3Service.moveToPermanent(imageUrl)
            article.setAuthorSlug(authorSlug);
            article.setTitleSlug(generateSlug(titleDisplay));

            articleDao.insert(article);
            Article result = articleDao.findById(article.getId());
            tx.commit();
            initAuthorFields(result);
            return result;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException(e);
        }
    }

    @Override
    public Article updateArticle(Long id, ArticleUpdateRequest request, String authorSlug) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Article existing = articleDao.findById(id);
            if (existing == null || !existing.getAuthorSlug().equals(authorSlug)) {
                tx.commit();
                return null;
            }

            String summary = request.getSummary();
            if (summary == null || summary.trim().isEmpty()) {
                tx.commit();
                return null;
            }

            Article article = new Article();
            article.setId(id);
            article.setTitleDisplay(request.getTitleDisplay());
            article.setSummary(summary);
            article.setContent(request.getContent());
            article.setCategory(request.getCategory());
            article.setImageUrl(request.getImageUrl()); // Direct assignment instead of s3Service.moveToPermanent()
            article.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

            Timestamp expectedUpdatedAt = parseUpdatedAt(request.getUpdatedAt(), existing.getUpdatedAt());

            int rows = articleDao.update(article, expectedUpdatedAt);
            if (rows == 0) {
                tx.commit();
                return null;
            }
            Article result = articleDao.findById(id);
            tx.commit();
            initAuthorFields(result);
            return result;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException(e);
        }
    }

    @Override
    public String deleteArticle(Long id, String authorSlug) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Article article = articleDao.findById(id);
            if (article == null) {
                tx.commit();
                return "文章不存在";
            }
            if (!authorSlug.equals(article.getAuthorSlug())) {
                tx.commit();
                return "無權限刪除此文章";
            }
            articleDao.delete(article);
            tx.commit();
            return null;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException(e);
        }
    }

    private String generateSlug(String title) {
        String slug = title.toLowerCase()
            .replaceAll("[^a-z0-9\\u4e00-\\u9fa5]+", "-")
            .replaceAll("^-+|-+$", "");
        String suffix = UUID.randomUUID().toString().substring(0, 4);
        return slug + "-" + suffix;
    }

    private Timestamp parseUpdatedAt(Timestamp updatedAt, Timestamp fallback) {
        return updatedAt != null ? updatedAt : fallback;
    }
}
