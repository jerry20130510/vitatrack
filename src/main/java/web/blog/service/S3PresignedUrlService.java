package web.blog.service;

import java.util.Map;

public interface S3PresignedUrlService {
    Map<String, Object> generatePresignedUrl(String articleSlug, String fileExtension);
    String moveToPermanent(String imageUrl);
    void configureLifecyclePolicy();
}
