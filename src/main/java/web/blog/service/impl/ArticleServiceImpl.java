package web.blog.service.impl;

import web.blog.vo.Article;
import web.blog.vo.ArticleListResponse;
import web.blog.dao.ArticleDao;
import web.blog.dao.impl.ArticleDaoImpl;
import web.blog.service.ArticleService;

import java.util.List;

public class ArticleServiceImpl implements ArticleService {
    private ArticleDao articleDao;

    public ArticleServiceImpl() {
        this.articleDao = new ArticleDaoImpl();
    }

    @Override
    public ArticleListResponse listArticles(int page, int size, String category, String authorSlug) {
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
    }

    @Override
    public Article getArticleDetail(String titleSlug) {
        return articleDao.findByTitleSlugWithAuthor(titleSlug);
    }

    @Override
    public String incrementViews(String titleSlug) {
        int rows = articleDao.incrementViews(titleSlug);
        return rows > 0 ? null : "文章不存在";
    }

    @Override
    public String incrementLikes(String titleSlug) {
        int rows = articleDao.incrementLikes(titleSlug);
        return rows > 0 ? null : "文章不存在";
    }

    @Override
    public String incrementShares(String titleSlug) {
        int rows = articleDao.incrementShares(titleSlug);
        return rows > 0 ? null : "文章不存在";
    }

    @Override
    public int getViews(String titleSlug) {
        return articleDao.getTotalViews(titleSlug);
    }

    @Override
    public int getLikes(String titleSlug) {
        return articleDao.getTotalLikes(titleSlug);
    }

    @Override
    public int getShares(String titleSlug) {
        return articleDao.getTotalShares(titleSlug);
    }
}
