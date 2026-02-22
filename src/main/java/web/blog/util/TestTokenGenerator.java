package web.blog.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.sql.Timestamp;

public class TestTokenGenerator {
    public static void main(String[] args) {
        String accessTokenSecret = "healthy-blog-access-secret-key-2026";
        int accessTokenExpiry = 1800;

        long now = System.currentTimeMillis();
        String token = JWT.create()
                .withSubject("test@example.com")
                .withClaim("email", "test@example.com")
                .withClaim("authorSlug", "tsung-wu-lin-6552")
                .withClaim("displayName", "Tsung Wu Lin")
                .withIssuedAt(new Timestamp(now))
                .withExpiresAt(new Timestamp(now + accessTokenExpiry * 1000L))
                .sign(Algorithm.HMAC256(accessTokenSecret));

        System.out.println(token);
    }
}
