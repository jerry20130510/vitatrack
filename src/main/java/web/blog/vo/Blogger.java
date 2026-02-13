package web.blog.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "bloggers")
@Getter
@Setter
@NoArgsConstructor
public class Blogger implements Serializable {
    @Id
    @Column(nullable = false)
    private String email;

    @Column(name = "author_slug", nullable = false, unique = true)
    private String authorSlug;

    @Column(name = "google_email")
    private String googleEmail;

    @Column(name = "google_sub")
    private String googleSub;

    @Column(name = "azure_email")
    private String azureEmail;

    @Column(name = "azure_sub")
    private String azureSub;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "profile_image")
    private String profileImage;

    private String role;

    @Column(name = "profile_complete")
    private Boolean profileComplete;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "last_login")
    private Timestamp lastLogin;
}
