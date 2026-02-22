package web.blog.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface CookieService {
    void setAccessTokenCookie(HttpServletResponse response, String token);
    void setRefreshTokenCookie(HttpServletResponse response, String token);
    String getAccessToken(HttpServletRequest request);
    String getRefreshToken(HttpServletRequest request);
    void clearAuthCookies(HttpServletResponse response);
    void setStateCookie(HttpServletResponse response, String state);
    void clearStateCookie(HttpServletResponse response);
    void setReturnUrlCookie(HttpServletResponse response, String returnUrl);
    void deleteReturnUrlCookie(HttpServletResponse response);
}
