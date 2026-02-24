package web.blog.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import web.blog.vo.Blogger;

public interface JwtTokenService {
    String createAccessToken(Blogger blogger);
    String createRefreshToken(Blogger blogger);
    DecodedJWT validateAccessToken(String token);
    DecodedJWT validateRefreshToken(String token);
    String getEmail(DecodedJWT token);
    String getAuthorSlug(DecodedJWT token);
    String getDisplayName(DecodedJWT token);
}
