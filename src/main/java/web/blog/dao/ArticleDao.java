package web.blog.dao;

import web.blog.vo.Article;

import java.sql.Timestamp;
import java.util.List;

public interface ArticleDao {
    List<Article> findAll(int offset, int limit);
    List<Article> findByCategory(String category, int offset, int limit);
    List<Article> findByAuthorSlug(String authorSlug, int offset, int limit);
    int countAll();
    int countByCategory(String category);
    int countByAuthorSlug(String authorSlug);
    Article findByTitleSlugWithAuthor(String titleSlug);
    int incrementViews(String titleSlug);
    int incrementLikes(String titleSlug);
    int incrementShares(String titleSlug);
    int getTotalViews(String titleSlug);
    int getTotalLikes(String titleSlug);
    int getTotalShares(String titleSlug);

    Article findById(Long id);
    int insert(Article article);
    int update(Article article, Timestamp expectedUpdatedAt);
    void delete(Article article);
}
