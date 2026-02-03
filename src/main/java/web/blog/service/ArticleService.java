package web.blog.service;

import web.blog.bean.Article;
import web.blog.bean.ArticleListResponse;

public interface ArticleService {
    ArticleListResponse listArticles(int page, int size, String category, String authorSlug);
    Article getArticleDetail(String titleSlug);
    String incrementViews(String titleSlug);
    String incrementLikes(String titleSlug);
    String incrementShares(String titleSlug);
    int getViews(String titleSlug);
    int getLikes(String titleSlug);
    int getShares(String titleSlug);
}
