package web.blog.service.impl;

import com.auth0.jwt.interfaces.DecodedJWT;
import web.blog.service.AuthService;
import web.blog.service.JwtTokenService;

import javax.naming.NamingException;
import java.util.Map;

public class AuthServiceImpl implements AuthService {
    private static final String KEY_AUTHENTICATED = "authenticated";
    private static final String KEY_AUTHOR_SLUG = "authorSlug";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_DISPLAY_NAME = "displayName";
    private static final Map<String, Object> UNAUTHENTICATED_RESPONSE = Map.of(KEY_AUTHENTICATED, false);
    
    private final JwtTokenService jwtTokenService;

    public AuthServiceImpl() throws NamingException {
        this.jwtTokenService = new JwtTokenServiceImpl();
    }

    @Override
    public Map<String, Object> getAuthStatus(String accessToken) {
        if (accessToken == null) {
            return UNAUTHENTICATED_RESPONSE;
        }
        DecodedJWT decoded = jwtTokenService.validateAccessToken(accessToken);
        if (decoded == null) {
            return UNAUTHENTICATED_RESPONSE;
        }
        return Map.of(
            KEY_AUTHENTICATED, true,
            KEY_AUTHOR_SLUG, jwtTokenService.getAuthorSlug(decoded),
            KEY_EMAIL, jwtTokenService.getEmail(decoded),
            KEY_DISPLAY_NAME, jwtTokenService.getDisplayName(decoded)
        );
    }
}
