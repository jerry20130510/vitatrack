package web.blog.service.impl;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import web.blog.service.S3PresignedUrlService;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;

public class S3PresignedUrlServiceImpl implements S3PresignedUrlService {
    private S3Presigner presigner;
    private S3Client s3Client;
    private String bucketName;
    
    private static final int PRESIGNED_URL_EXPIRY_MINUTES = 15;
    private static final int PRESIGNED_URL_EXPIRY_SECONDS = PRESIGNED_URL_EXPIRY_MINUTES * 60;
    private static final String TEMP_FOLDER_PREFIX = "articles/temp/";
    private static final String PERMANENT_FOLDER_PREFIX = "articles/";
    private static final int TEMP_FILE_EXPIRY_DAYS = 3;
    private static final String LIFECYCLE_RULE_ID = "DeleteOrphanedArticleImages";
    private static final int UUID_SUFFIX_LENGTH = 4;

    public S3PresignedUrlServiceImpl() throws NamingException {
        InitialContext ctx = new InitialContext();
        String region = (String) ctx.lookup("java:comp/env/aws.s3.region");
        String accessKeyId = (String) ctx.lookup("java:comp/env/aws.s3.access-key-id");
        String secretAccessKey = (String) ctx.lookup("java:comp/env/aws.s3.secret-access-key");
        bucketName = (String) ctx.lookup("java:comp/env/aws.s3.bucket");

        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey);
        presigner = S3Presigner.builder()
            .region(Region.of(region))
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .build();
        s3Client = S3Client.builder()
            .region(Region.of(region))
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .build();
    }

    @Override
    public Map<String, Object> generatePresignedUrl(String articleSlug, String fileExtension) {
        String suffix = UUID.randomUUID().toString().substring(0, UUID_SUFFIX_LENGTH);
        String key = String.format("%s%s-%s.%s", TEMP_FOLDER_PREFIX, articleSlug, suffix, fileExtension);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(PRESIGNED_URL_EXPIRY_MINUTES))
            .putObjectRequest(putObjectRequest)
            .build();

        PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);
        return Map.of("uploadUrl", presignedRequest.url().toString(), "expiresIn", PRESIGNED_URL_EXPIRY_SECONDS);
    }

    @Override
    public String moveToPermanent(String imageUrl) {
        if (imageUrl == null || !imageUrl.contains(TEMP_FOLDER_PREFIX)) {
            return imageUrl;
        }
        try {
            String tempKey = imageUrl.substring(imageUrl.indexOf(TEMP_FOLDER_PREFIX));
            String permanentKey = tempKey.replace(TEMP_FOLDER_PREFIX, PERMANENT_FOLDER_PREFIX);
            s3Client.copyObject(CopyObjectRequest.builder()
                .sourceBucket(bucketName)
                .sourceKey(tempKey)
                .destinationBucket(bucketName)
                .destinationKey(permanentKey)
                .build());
            s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(tempKey)
                .build());
            return imageUrl.replace(TEMP_FOLDER_PREFIX, PERMANENT_FOLDER_PREFIX);
        } catch (Exception e) {
            throw new RuntimeException("Failed to move image from temp", e);
        }
    }

    @Override
    public void configureLifecyclePolicy() {
        try {
            LifecycleRule rule = LifecycleRule.builder()
                .id(LIFECYCLE_RULE_ID)
                .status(ExpirationStatus.ENABLED)
                .filter(LifecycleRuleFilter.builder()
                    .prefix(TEMP_FOLDER_PREFIX)
                    .build())
                .expiration(LifecycleExpiration.builder()
                    .days(TEMP_FILE_EXPIRY_DAYS)
                    .build())
                .build();
            BucketLifecycleConfiguration config = BucketLifecycleConfiguration.builder()
                .rules(rule)
                .build();
            s3Client.putBucketLifecycleConfiguration(
                PutBucketLifecycleConfigurationRequest.builder()
                    .bucket(bucketName)
                    .lifecycleConfiguration(config)
                    .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to configure S3 lifecycle policy", e);
        }
    }
}
