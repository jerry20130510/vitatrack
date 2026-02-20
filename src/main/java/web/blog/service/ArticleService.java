package web.blog.service;

import web.blog.dto.ArticleCreateRequest;
import web.blog.dto.ArticleUpdateRequest;
import web.blog.dto.ArticleListResponse;
import web.blog.vo.Article;

public interface ArticleService {
    ArticleListResponse listArticles(int page, int size, String category, String authorSlug);
    Article getArticleDetail(String titleSlug);

    ArticleListResponse listArticlesAdmin(String authorSlug, int page, int size);
    Article getArticleDetailAdmin(Long id, String authorSlug);
    Article createArticle(ArticleCreateRequest request, String authorSlug);
    Article updateArticle(Long id, ArticleUpdateRequest request, String authorSlug);
    String deleteArticle(Long id, String authorSlug);
}
