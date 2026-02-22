package web.blog.service;

import java.util.Map;

public interface AuthService {
    Map<String, Object> getAuthStatus(String accessToken);
}
