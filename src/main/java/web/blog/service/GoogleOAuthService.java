package web.blog.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

import java.util.Map;

public interface GoogleOAuthService {
    String getAuthorizationUrl(String state);
    Map<String, String> exchangeCodeForTokens(String code);
    GoogleIdToken.Payload verifyIdToken(String idTokenString) throws Exception;
}
