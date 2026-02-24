package web.blog.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
public class ArticlePublicResponse {
    private Long id;
    private String titleSlug;
    private String titleDisplay;
    private String summary;
    private String content;
    private String imageUrl;
    private String category;
    private String authorSlug;
    private int totalLikes;
    private int totalViews;
    private int totalShares;
    private Timestamp createdAt;
    private String authorDisplayName;
    private String authorProfileImage;
}
