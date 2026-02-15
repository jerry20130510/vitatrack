package web.blog.service;

public interface ArticleEngagementService {
    String incrementViews(String slug);
    String incrementLikes(String slug);
    String incrementShares(String slug);
    int getLikes(String slug);
    int getShares(String slug);
}
