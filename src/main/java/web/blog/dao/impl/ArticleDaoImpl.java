package web.blog.dao.impl;

import web.blog.vo.Article;
import web.blog.dao.ArticleDao;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArticleDaoImpl implements ArticleDao {
    private DataSource ds;

    public ArticleDaoImpl() throws NamingException {
        Context ctx = new InitialContext();
        ds = (DataSource) ctx.lookup("java:comp/env/jdbc/vitatrack");
    }

    @Override
    public List<Article> findAll(int offset, int limit) {
        String sql = "SELECT a.id, a.title_slug, a.title_display, a.summary, a.image_url, " +
                    "a.category, a.author_slug, a.total_views, a.total_likes, a.total_shares, " +
                    "a.created_at, a.updated_at, " +
                    "b.display_name AS author_display_name, b.profile_image AS author_profile_image " +
                    "FROM articles a " +
                    "LEFT JOIN bloggers b ON a.author_slug = b.author_slug " +
                    "ORDER BY a.created_at DESC " +
                    "LIMIT ? OFFSET ?";
        
        List<Article> articles = new ArrayList<>();
        
        try (Connection conn = ds.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, limit);
            pstmt.setInt(2, offset);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Article article = new Article();
                    article.setId(rs.getLong("id"));
                    article.setTitleSlug(rs.getString("title_slug"));
                    article.setTitleDisplay(rs.getString("title_display"));
                    article.setSummary(rs.getString("summary"));
                    article.setCategory(rs.getString("category"));
                    article.setAuthorSlug(rs.getString("author_slug"));
                    article.setTotalViews(rs.getInt("total_views"));
                    article.setTotalLikes(rs.getInt("total_likes"));
                    article.setTotalShares(rs.getInt("total_shares"));
                    
                    String imageUrl = rs.getString("image_url");
                    if (imageUrl != null) {
                        article.setImageUrl(imageUrl);
                    }
                    
                    Timestamp createdAt = rs.getTimestamp("created_at");
                    if (createdAt != null) {
                        article.setCreatedAt(createdAt);
                    }
                    
                    Timestamp updatedAt = rs.getTimestamp("updated_at");
                    if (updatedAt != null) {
                        article.setUpdatedAt(updatedAt);
                    }
                    
                    String authorDisplayName = rs.getString("author_display_name");
                    if (authorDisplayName != null) {
                        article.setAuthorDisplayName(authorDisplayName);
                    }
                    String authorProfileImage = rs.getString("author_profile_image");
                    if (authorProfileImage != null) {
                        article.setAuthorProfileImage(authorProfileImage);
                    }
                    
                    articles.add(article);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return articles;
    }

    @Override
    public List<Article> findByCategory(String category, int offset, int limit) {
        String sql = "SELECT a.id, a.title_slug, a.title_display, a.summary, a.image_url, " +
                    "a.category, a.author_slug, a.total_views, a.total_likes, a.total_shares, " +
                    "a.created_at, a.updated_at, " +
                    "b.display_name AS author_display_name, b.profile_image AS author_profile_image " +
                    "FROM articles a " +
                    "LEFT JOIN bloggers b ON a.author_slug = b.author_slug " +
                    "WHERE a.category = ? " +
                    "ORDER BY a.created_at DESC " +
                    "LIMIT ? OFFSET ?";
        
        List<Article> articles = new ArrayList<>();
        
        try (Connection conn = ds.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, category);
            pstmt.setInt(2, limit);
            pstmt.setInt(3, offset);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Article article = new Article();
                    article.setId(rs.getLong("id"));
                    article.setTitleSlug(rs.getString("title_slug"));
                    article.setTitleDisplay(rs.getString("title_display"));
                    article.setSummary(rs.getString("summary"));
                    article.setCategory(rs.getString("category"));
                    article.setAuthorSlug(rs.getString("author_slug"));
                    article.setTotalViews(rs.getInt("total_views"));
                    article.setTotalLikes(rs.getInt("total_likes"));
                    article.setTotalShares(rs.getInt("total_shares"));
                    
                    String imageUrl = rs.getString("image_url");
                    if (imageUrl != null) {
                        article.setImageUrl(imageUrl);
                    }
                    
                    Timestamp createdAt = rs.getTimestamp("created_at");
                    if (createdAt != null) {
                        article.setCreatedAt(createdAt);
                    }
                    
                    Timestamp updatedAt = rs.getTimestamp("updated_at");
                    if (updatedAt != null) {
                        article.setUpdatedAt(updatedAt);
                    }
                    
                    String authorDisplayName = rs.getString("author_display_name");
                    if (authorDisplayName != null) {
                        article.setAuthorDisplayName(authorDisplayName);
                    }
                    String authorProfileImage = rs.getString("author_profile_image");
                    if (authorProfileImage != null) {
                        article.setAuthorProfileImage(authorProfileImage);
                    }
                    
                    articles.add(article);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return articles;
    }

    @Override
    public List<Article> findByAuthorSlug(String authorSlug, int offset, int limit) {
        String sql = "SELECT a.id, a.title_slug, a.title_display, a.summary, a.image_url, " +
                    "a.category, a.author_slug, a.total_views, a.total_likes, a.total_shares, " +
                    "a.created_at, a.updated_at, " +
                    "b.display_name AS author_display_name, b.profile_image AS author_profile_image " +
                    "FROM articles a " +
                    "LEFT JOIN bloggers b ON a.author_slug = b.author_slug " +
                    "WHERE a.author_slug = ? " +
                    "ORDER BY a.created_at DESC " +
                    "LIMIT ? OFFSET ?";
        
        List<Article> articles = new ArrayList<>();
        
        try (Connection conn = ds.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, authorSlug);
            pstmt.setInt(2, limit);
            pstmt.setInt(3, offset);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Article article = new Article();
                    article.setId(rs.getLong("id"));
                    article.setTitleSlug(rs.getString("title_slug"));
                    article.setTitleDisplay(rs.getString("title_display"));
                    article.setSummary(rs.getString("summary"));
                    article.setCategory(rs.getString("category"));
                    article.setAuthorSlug(rs.getString("author_slug"));
                    article.setTotalViews(rs.getInt("total_views"));
                    article.setTotalLikes(rs.getInt("total_likes"));
                    article.setTotalShares(rs.getInt("total_shares"));
                    
                    String imageUrl = rs.getString("image_url");
                    if (imageUrl != null) {
                        article.setImageUrl(imageUrl);
                    }
                    
                    Timestamp createdAt = rs.getTimestamp("created_at");
                    if (createdAt != null) {
                        article.setCreatedAt(createdAt);
                    }
                    
                    Timestamp updatedAt = rs.getTimestamp("updated_at");
                    if (updatedAt != null) {
                        article.setUpdatedAt(updatedAt);
                    }
                    
                    String authorDisplayName = rs.getString("author_display_name");
                    if (authorDisplayName != null) {
                        article.setAuthorDisplayName(authorDisplayName);
                    }
                    String authorProfileImage = rs.getString("author_profile_image");
                    if (authorProfileImage != null) {
                        article.setAuthorProfileImage(authorProfileImage);
                    }
                    
                    articles.add(article);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return articles;
    }

    @Override
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM articles";
        
        try (Connection conn = ds.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }

    @Override
    public int countByCategory(String category) {
        String sql = "SELECT COUNT(*) FROM articles WHERE category = ?";
        
        try (Connection conn = ds.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, category);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }

    @Override
    public int countByAuthorSlug(String authorSlug) {
        String sql = "SELECT COUNT(*) FROM articles WHERE author_slug = ?";
        
        try (Connection conn = ds.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, authorSlug);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }

    @Override
    public Article findByTitleSlugWithAuthor(String titleSlug) {
        String sql = "SELECT a.id, a.title_slug, a.title_display, a.summary, a.content, a.image_url, " +
                    "a.category, a.author_slug, a.total_views, a.total_likes, a.total_shares, " +
                    "a.created_at, a.updated_at, " +
                    "b.display_name AS author_display_name, b.profile_image AS author_profile_image " +
                    "FROM articles a " +
                    "LEFT JOIN bloggers b ON a.author_slug = b.author_slug " +
                    "WHERE a.title_slug = ?";
        
        try (Connection conn = ds.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, titleSlug);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Article article = new Article();
                    article.setId(rs.getLong("id"));
                    article.setTitleSlug(rs.getString("title_slug"));
                    article.setTitleDisplay(rs.getString("title_display"));
                    article.setSummary(rs.getString("summary"));
                    article.setCategory(rs.getString("category"));
                    article.setAuthorSlug(rs.getString("author_slug"));
                    article.setTotalViews(rs.getInt("total_views"));
                    article.setTotalLikes(rs.getInt("total_likes"));
                    article.setTotalShares(rs.getInt("total_shares"));
                    
                    String content = rs.getString("content");
                    if (content != null) {
                        article.setContent(content);
                    }
                    
                    String imageUrl = rs.getString("image_url");
                    if (imageUrl != null) {
                        article.setImageUrl(imageUrl);
                    }
                    
                    Timestamp createdAt = rs.getTimestamp("created_at");
                    if (createdAt != null) {
                        article.setCreatedAt(createdAt);
                    }
                    
                    Timestamp updatedAt = rs.getTimestamp("updated_at");
                    if (updatedAt != null) {
                        article.setUpdatedAt(updatedAt);
                    }
                    
                    String authorDisplayName = rs.getString("author_display_name");
                    if (authorDisplayName != null) {
                        article.setAuthorDisplayName(authorDisplayName);
                    }
                    String authorProfileImage = rs.getString("author_profile_image");
                    if (authorProfileImage != null) {
                        article.setAuthorProfileImage(authorProfileImage);
                    }
                    
                    return article;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    @Override
    public int incrementViews(String titleSlug) {
        String sql = "UPDATE articles SET total_views = total_views + 1 WHERE title_slug = ?";
        
        try (Connection conn = ds.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, titleSlug);
            return pstmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }

    @Override
    public int incrementLikes(String titleSlug) {
        String sql = "UPDATE articles SET total_likes = total_likes + 1 WHERE title_slug = ?";
        
        try (Connection conn = ds.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, titleSlug);
            return pstmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }

    @Override
    public int incrementShares(String titleSlug) {
        String sql = "UPDATE articles SET total_shares = total_shares + 1 WHERE title_slug = ?";
        
        try (Connection conn = ds.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, titleSlug);
            return pstmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }

    @Override
    public int getTotalViews(String titleSlug) {
        String sql = "SELECT total_views FROM articles WHERE title_slug = ?";
        
        try (Connection conn = ds.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, titleSlug);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }

    @Override
    public int getTotalLikes(String titleSlug) {
        String sql = "SELECT total_likes FROM articles WHERE title_slug = ?";
        
        try (Connection conn = ds.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, titleSlug);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }

    @Override
    public int getTotalShares(String titleSlug) {
        String sql = "SELECT total_shares FROM articles WHERE title_slug = ?";
        
        try (Connection conn = ds.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, titleSlug);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
}
