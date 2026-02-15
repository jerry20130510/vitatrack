package web.blog.vo;

import lombok.AccessLevel;
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

    @Column(name = "total_likes", insertable = false)
    private int totalLikes;

    @Column(name = "total_views", insertable = false)
    private int totalViews;

    @Column(name = "total_shares", insertable = false)
    private int totalShares;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private Timestamp updatedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_slug", referencedColumnName = "author_slug", insertable = false, updatable = false)
    private Blogger blogger;

    @Getter(AccessLevel.NONE)
    @Setter
    @javax.persistence.Transient
    private String authorDisplayName;

    @Getter(AccessLevel.NONE)
    @Setter
    @javax.persistence.Transient
    private String authorProfileImage;

    public String getAuthorDisplayName() {
        return authorDisplayName != null ? authorDisplayName : (blogger != null ? blogger.getDisplayName() : null);
    }

    public String getAuthorProfileImage() {
        return authorProfileImage != null ? authorProfileImage : (blogger != null ? blogger.getProfileImage() : null);
    }
}
