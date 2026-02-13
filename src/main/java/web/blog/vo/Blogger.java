package web.blog.vo;

import java.sql.Timestamp;

public class Blogger {
    private String email;
    private String authorSlug;
    private String googleEmail;
    private String googleSub;
    private String azureEmail;
    private String azureSub;
    private String displayName;
    private String profileImage;
    private String role;
    private Boolean profileComplete;
    private Timestamp createdAt;
    private Timestamp lastLogin;

    public Blogger() {}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAuthorSlug() {
        return authorSlug;
    }

    public void setAuthorSlug(String authorSlug) {
        this.authorSlug = authorSlug;
    }

    public String getGoogleEmail() {
        return googleEmail;
    }

    public void setGoogleEmail(String googleEmail) {
        this.googleEmail = googleEmail;
    }

    public String getGoogleSub() {
        return googleSub;
    }

    public void setGoogleSub(String googleSub) {
        this.googleSub = googleSub;
    }

    public String getAzureEmail() {
        return azureEmail;
    }

    public void setAzureEmail(String azureEmail) {
        this.azureEmail = azureEmail;
    }

    public String getAzureSub() {
        return azureSub;
    }

    public void setAzureSub(String azureSub) {
        this.azureSub = azureSub;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getProfileComplete() {
        return profileComplete;
    }

    public void setProfileComplete(Boolean profileComplete) {
        this.profileComplete = profileComplete;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }
}
