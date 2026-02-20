package web.blog.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ArticleCreateRequest {
    private String titleDisplay;
    private String summary;
    private String content;
    private String category;
    private String imageUrl;
}
