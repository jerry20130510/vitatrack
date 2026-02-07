package web.blog.service.impl;

import web.blog.vo.Article;
import web.blog.vo.ArticleListResponse;
import web.blog.dao.ArticleDao;
import web.blog.dao.impl.ArticleDaoImpl;
import web.blog.service.ArticleService;

import javax.naming.NamingException;
import java.util.List;

public class ArticleServiceImpl implements ArticleService {
    private ArticleDao articleDao;

    public ArticleServiceImpl() throws NamingException {
        this.articleDao = new ArticleDaoImpl();
    }

    @Override
    public ArticleListResponse listArticles(int page, int size, String category, String authorSlug) {
        try {
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
            return new ArticleListResponse(page, size, totalElements, totalPages, articles);
            
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to retrieve article list", e);
        }
    }

    @Override
    public Article getArticleDetail(String titleSlug) {
        try {
            return articleDao.findByTitleSlugWithAuthor(titleSlug);
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to retrieve article details", e);
        }
    }

    @Override
    public String incrementViews(String titleSlug) {
        try {
            int rows = articleDao.incrementViews(titleSlug);
            return rows > 0 ? null : "文章不存在";
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to increment article views", e);
        }
    }

    @Override
    public String incrementLikes(String titleSlug) {
        try {
            int rows = articleDao.incrementLikes(titleSlug);
            return rows > 0 ? null : "文章不存在";
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to increment article likes", e);
        }
    }

    @Override
    public String incrementShares(String titleSlug) {
        try {
            int rows = articleDao.incrementShares(titleSlug);
            return rows > 0 ? null : "文章不存在";
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to increment article shares", e);
        }
    }

    @Override
    public int getViews(String titleSlug) {
        try {
            return articleDao.getTotalViews(titleSlug);
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to get article views", e);
        }
    }

    @Override
    public int getLikes(String titleSlug) {
        try {
            return articleDao.getTotalLikes(titleSlug);
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to get article likes", e);
        }
    }

    @Override
    public int getShares(String titleSlug) {
        try {
            return articleDao.getTotalShares(titleSlug);
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to get article shares", e);
        }
    }
}
