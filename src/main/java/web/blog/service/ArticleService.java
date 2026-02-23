package web.blog.service;

import web.blog.dto.ArticleCreateRequest;
import web.blog.dto.ArticleUpdateRequest;
import web.blog.dto.ArticleListResponse;
import web.blog.dto.ArticlePublicResponse;
import web.blog.dto.ArticleAdminResponse;

public interface ArticleService {
    ArticleListResponse<ArticlePublicResponse> listArticles(int page, int size, String category, String authorSlug);
    ArticlePublicResponse getArticleDetail(String titleSlug);

    ArticleListResponse<ArticleAdminResponse> listArticlesAdmin(String authorSlug, int page, int size);
    ArticleAdminResponse getArticleDetailAdmin(Long id, String authorSlug);
    ArticleAdminResponse createArticle(ArticleCreateRequest request, String authorSlug);
    ArticleAdminResponse updateArticle(Long id, ArticleUpdateRequest request, String authorSlug);
    String deleteArticle(Long id, String authorSlug);
}
