package web.blog.service.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.gson.Gson;
import web.blog.service.GoogleOAuthService;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class GoogleOAuthServiceImpl implements GoogleOAuthService {
    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;
    private final HttpClient httpClient;
    private static final Gson gson = new Gson();

    private static final String AUTH_URL = "https://accounts.google.com/o/oauth2/v2/auth";
    private static final String TOKEN_URL = "https://oauth2.googleapis.com/token";
    private static final String OAUTH_SCOPE = "openid email profile";
    private static final String RESPONSE_TYPE = "code";
    private static final String ACCESS_TYPE = "offline";
    private static final String PROMPT = "consent";
    private static final String GRANT_TYPE = "authorization_code";
    private static final String CONTENT_TYPE = "application/x-www-form-urlencoded";
    private static final String TOKEN_ID = "id_token";
    private static final String TOKEN_ACCESS = "access_token";
    private static final String TOKEN_REFRESH = "refresh_token";

    /**
     * Response DTO for Google OAuth token exchange.
     * 
     * Example JSON received from Google:
     * {
     *   "access_token": "ya29.a0AfH6SMBx...",
     *   "expires_in": 3599,
     *   "scope": "openid https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile",
     *   "token_type": "Bearer",
     *   "id_token": "eyJhbGciOiJSUzI1NiIsImtpZCI6IjI3...",
     *   "refresh_token": "1//0gL3qZ9X..."
     * }
     * 
     * Note: refresh_token is only included on first authorization or when prompt=consent
     */
    private static class TokenResponse {
        private String id_token;
        private String access_token;
        private String refresh_token;
        
        String getIdToken() { return id_token; }
        String getAccessToken() { return access_token; }
        String getRefreshToken() { return refresh_token; }
    }

    public GoogleOAuthServiceImpl() throws NamingException {
        InitialContext ctx = new InitialContext();
        this.clientId = (String) ctx.lookup("java:comp/env/google/client-id");
        this.clientSecret = (String) ctx.lookup("java:comp/env/google/client-secret");
        this.redirectUri = (String) ctx.lookup("java:comp/env/google/redirect-uri");
        this.httpClient = HttpClient.newHttpClient();
    }

    @Override
    public String getAuthorizationUrl(String state) {
        Map<String, String> params = Map.of(
            "client_id", clientId,
            "redirect_uri", redirectUri,
            "response_type", RESPONSE_TYPE,
            "scope", OAUTH_SCOPE,
            "state", state,
            "access_type", ACCESS_TYPE,
            "prompt", PROMPT
        );
        return AUTH_URL + "?" + buildQueryString(params);
    }

    /**
     * Exchange authorization code for OAuth tokens.
     * 
     * Example request body sent to Google (URL-encoded):
     * code=4/0AY0e-g7X...
     * &client_id=123456789.apps.googleusercontent.com
     * &client_secret=GOCSPX-abc123...
     * &redirect_uri=http://localhost:8080/oauth/google/callback
     * &grant_type=authorization_code
     */
    @Override
    public Map<String, String> exchangeCodeForTokens(String code) {
        try {
            Map<String, String> params = Map.of(
                "code", code,
                "client_id", clientId,
                "client_secret", clientSecret,
                "redirect_uri", redirectUri,
                "grant_type", GRANT_TYPE
            );

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(TOKEN_URL))
                .header("Content-Type", CONTENT_TYPE)
                .POST(HttpRequest.BodyPublishers.ofString(buildQueryString(params)))
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() != 200) {
                throw new RuntimeException("Token exchange failed with status: " + response.statusCode());
            }

            TokenResponse tokenResponse = gson.fromJson(response.body(), TokenResponse.class);
            Map<String, String> result = new HashMap<>();
            result.put(TOKEN_ID, tokenResponse.getIdToken());
            result.put(TOKEN_ACCESS, tokenResponse.getAccessToken());
            if (tokenResponse.getRefreshToken() != null) {
                result.put(TOKEN_REFRESH, tokenResponse.getRefreshToken());
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Failed to exchange code for tokens", e);
        }
    }

    @Override
    public GoogleIdToken.Payload verifyIdToken(String idTokenString) throws Exception {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
            new NetHttpTransport(), GsonFactory.getDefaultInstance())
            .setAudience(Collections.singletonList(clientId))
            .build();

        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken == null) {
            throw new RuntimeException("Invalid ID token");
        }
        return idToken.getPayload();
    }

    private String buildQueryString(Map<String, String> params) {
        return params.entrySet().stream()
            .map(entry -> URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8) + 
                         "=" + 
                         URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
            .collect(Collectors.joining("&"));
    }
}
