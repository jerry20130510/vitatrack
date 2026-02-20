package web.blog.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
public class ArticleUpdateRequest {
    private String titleDisplay;
    private String summary;
    private String content;
    private String category;
    private String imageUrl;
    private Timestamp updatedAt;
}
