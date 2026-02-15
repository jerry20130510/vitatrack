package web.blog.service;

import web.blog.vo.Article;
import web.blog.vo.ArticleListResponse;

import java.util.Map;

public interface ArticleService {
    ArticleListResponse listArticles(int page, int size, String category, String authorSlug);
    Article getArticleDetail(String titleSlug);
    String incrementViews(String titleSlug);
    String incrementLikes(String titleSlug);
    String incrementShares(String titleSlug);
    int getViews(String titleSlug);
    int getLikes(String titleSlug);
    int getShares(String titleSlug);

    ArticleListResponse listArticlesAdmin(String authorSlug, int page, int size);
    Article getArticleDetailAdmin(Long id, String authorSlug);
    Article createArticle(Map<String, Object> request, String authorSlug);
    Article updateArticle(Long id, Map<String, Object> request, String authorSlug);
    String deleteArticle(Long id, String authorSlug);
}
