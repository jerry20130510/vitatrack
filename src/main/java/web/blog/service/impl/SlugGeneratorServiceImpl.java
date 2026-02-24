package web.blog.service.impl;

import com.google.gson.Gson;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;
import web.blog.service.SlugGeneratorService;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.List;
import java.util.UUID;

public class SlugGeneratorServiceImpl implements SlugGeneratorService {
    private BedrockRuntimeClient bedrockClient;
    private String modelId;
    private static final Gson gson = new Gson();

    /**
     * Request DTO for Bedrock Claude API.
     * 
     * Example JSON sent to Bedrock:
     * {
     *   "anthropic_version": "bedrock-2023-05-31",
     *   "max_tokens": 50,
     *   "messages": [
     *     {
     *       "role": "user",
     *       "content": "Convert this Chinese blog title to a short, SEO-friendly English URL slug..."
     *     }
     *   ]
     * }
     */
    private static class BedrockRequest {
        private final String anthropic_version = "bedrock-2023-05-31";
        private final int max_tokens = 50;
        private final List<Message> messages;
        
        static class Message {
            private final String role;
            private final String content;
            
            Message(String role, String content) {
                this.role = role;
                this.content = content;
            }
        }
        
        BedrockRequest(String prompt) {
            this.messages = List.of(new Message("user", prompt));
        }
    }
    
    /**
     * Response DTO for Bedrock Claude API.
     * 
     * Example JSON received from Bedrock:
     * {
     *   "id": "msg_01XYZ123",
     *   "type": "message",
     *   "role": "assistant",
     *   "content": [
     *     {
     *       "type": "text",
     *       "text": "healthy-lifestyle-habits"
     *     }
     *   ],
     *   "model": "claude-3-haiku-20240307",
     *   "stop_reason": "end_turn",
     *   "usage": {
     *     "input_tokens": 45,
     *     "output_tokens": 8
     *   }
     * }
     */
    private static class BedrockResponse {
        private List<Content> content;
        
        static class Content {
            private String text;
        }
        
        String getFirstText() {
            return content != null && !content.isEmpty() 
                ? content.get(0).text.trim() 
                : "";
        }
    }

    public SlugGeneratorServiceImpl() throws NamingException {
        InitialContext ctx = new InitialContext();
        String region = (String) ctx.lookup("java:comp/env/aws.bedrock.region");
        String accessKeyId = (String) ctx.lookup("java:comp/env/aws.bedrock.access-key-id");
        String secretAccessKey = (String) ctx.lookup("java:comp/env/aws.bedrock.secret-access-key");
        modelId = (String) ctx.lookup("java:comp/env/aws.bedrock.model-id");

        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey);
        bedrockClient = BedrockRuntimeClient.builder()
            .region(Region.of(region))
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .build();
    }

    @Override
    public String generateSlug(String chineseTitle) {
        try {
            String prompt = String.format(
                "Convert this Chinese blog title to a short, SEO-friendly English URL slug (3-5 words, lowercase, hyphens only): \"%s\". Respond with ONLY the slug, no explanation.",
                chineseTitle
            );

            BedrockRequest bedrockRequest = new BedrockRequest(prompt);
            String requestBody = gson.toJson(bedrockRequest);

            InvokeModelRequest request = InvokeModelRequest.builder()
                .modelId(modelId)
                .body(SdkBytes.fromUtf8String(requestBody))
                .build();

            InvokeModelResponse response = bedrockClient.invokeModel(request);
            String responseBody = response.body().asUtf8String();

            BedrockResponse bedrockResponse = gson.fromJson(responseBody, BedrockResponse.class);
            String slug = bedrockResponse.getFirstText();

            slug = slug.toLowerCase()
                .replaceAll("[^a-z0-9-]", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");

            String suffix = UUID.randomUUID().toString().substring(0, 4);
            return slug + "-" + suffix;

        } catch (Exception e) {
            return "article-" + UUID.randomUUID().toString().substring(0, 8);
        }
    }
}
