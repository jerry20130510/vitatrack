package web.blog.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import web.blog.service.JwtTokenService;
import web.blog.vo.Blogger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.sql.Timestamp;

public class JwtTokenServiceImpl implements JwtTokenService {
    private final String accessTokenSecret;
    private final String refreshTokenSecret;
    private final int accessTokenExpiry;
    private final int refreshTokenExpiry;

    public JwtTokenServiceImpl() throws NamingException {
        InitialContext ctx = new InitialContext();
        this.accessTokenSecret = (String) ctx.lookup("java:comp/env/jwt/access-secret");
        this.refreshTokenSecret = (String) ctx.lookup("java:comp/env/jwt/refresh-secret");
        this.accessTokenExpiry = (Integer) ctx.lookup("java:comp/env/jwt/access-expiry");
        this.refreshTokenExpiry = (Integer) ctx.lookup("java:comp/env/jwt/refresh-expiry");
    }

    @Override
    public String createAccessToken(Blogger blogger) {
        long now = System.currentTimeMillis();
        return JWT.create()
            .withSubject(blogger.getEmail())
            .withClaim("email", blogger.getEmail())
            .withClaim("authorSlug", blogger.getAuthorSlug())
            .withClaim("displayName", blogger.getDisplayName())
            .withIssuedAt(new Timestamp(now))
            .withExpiresAt(new Timestamp(now + accessTokenExpiry * 1000L))
            .sign(Algorithm.HMAC256(accessTokenSecret));
    }

    @Override
    public String createRefreshToken(Blogger blogger) {
        long now = System.currentTimeMillis();
        return JWT.create()
            .withSubject(blogger.getEmail())
            .withIssuedAt(new Timestamp(now))
            .withExpiresAt(new Timestamp(now + refreshTokenExpiry * 1000L))
            .sign(Algorithm.HMAC256(refreshTokenSecret));
    }

    @Override
    public DecodedJWT validateAccessToken(String token) {
        try {
            return JWT.require(Algorithm.HMAC256(accessTokenSecret))
                .build()
                .verify(token);
        } catch (JWTVerificationException e) {
            return null;
        }
    }

    @Override
    public DecodedJWT validateRefreshToken(String token) {
        try {
            return JWT.require(Algorithm.HMAC256(refreshTokenSecret))
                .build()
                .verify(token);
        } catch (JWTVerificationException e) {
            return null;
        }
    }

    @Override
    public String getEmail(DecodedJWT token) {
        return token.getClaim("email").asString();
    }

    @Override
    public String getAuthorSlug(DecodedJWT token) {
        return token.getClaim("authorSlug").asString();
    }

    @Override
    public String getDisplayName(DecodedJWT token) {
        return token.getClaim("displayName").asString();
    }
}
