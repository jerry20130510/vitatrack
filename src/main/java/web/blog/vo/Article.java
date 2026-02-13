package web.blog.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "articles")
@Getter
@Setter
@NoArgsConstructor
public class Article implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title_slug", nullable = false, unique = true)
    private String titleSlug;

    @Column(name = "title_display", nullable = false)
    private String titleDisplay;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "image_url")
    private String imageUrl;

    private String category;

    @Column(name = "author_slug", nullable = false)
    private String authorSlug;

    @Column(name = "total_likes")
    private int totalLikes;

    @Column(name = "total_views")
    private int totalViews;

    @Column(name = "total_shares")
    private int totalShares;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_slug", referencedColumnName = "author_slug", insertable = false, updatable = false)
    private Blogger blogger;
}
