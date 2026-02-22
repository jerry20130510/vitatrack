package web.blog.service.impl;

import web.blog.service.CookieService;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieServiceImpl implements CookieService {
    private static final String COOKIE_ACCESS_TOKEN = "access_token";
    private static final String COOKIE_REFRESH_TOKEN = "refresh_token";
    private static final String COOKIE_OAUTH_STATE = "oauth_state";
    private static final int OAUTH_STATE_EXPIRY_SECONDS = 60;
    
    private final int accessTokenExpiry;
    private final int refreshTokenExpiry;

    public CookieServiceImpl() throws NamingException {
        InitialContext ctx = new InitialContext();
        this.accessTokenExpiry = (Integer) ctx.lookup("java:comp/env/jwt/access-expiry");
        this.refreshTokenExpiry = (Integer) ctx.lookup("java:comp/env/jwt/refresh-expiry");
    }

    @Override
    public void setAccessTokenCookie(HttpServletResponse response, String token) {
        setCookie(response, COOKIE_ACCESS_TOKEN, token, accessTokenExpiry);
    }

    @Override
    public void setRefreshTokenCookie(HttpServletResponse response, String token) {
        setCookie(response, COOKIE_REFRESH_TOKEN, token, refreshTokenExpiry);
    }

    @Override
    public String getAccessToken(HttpServletRequest request) {
        return getCookieValue(request, COOKIE_ACCESS_TOKEN);
    }

    @Override
    public String getRefreshToken(HttpServletRequest request) {
        return getCookieValue(request, COOKIE_REFRESH_TOKEN);
    }

    @Override
    public void clearAuthCookies(HttpServletResponse response) {
        clearCookie(response, COOKIE_ACCESS_TOKEN);
        clearCookie(response, COOKIE_REFRESH_TOKEN);
    }

    @Override
    public void setStateCookie(HttpServletResponse response, String state) {
        setCookie(response, COOKIE_OAUTH_STATE, state, OAUTH_STATE_EXPIRY_SECONDS);
    }

    @Override
    public void clearStateCookie(HttpServletResponse response) {
        clearCookie(response, COOKIE_OAUTH_STATE);
    }

    @Override
    public void setReturnUrlCookie(HttpServletResponse response, String returnUrl) {
        setCookie(response, "return_url", returnUrl, OAUTH_STATE_EXPIRY_SECONDS);
    }

    @Override
    public void deleteReturnUrlCookie(HttpServletResponse response) {
        clearCookie(response, "return_url");
    }

    private void setCookie(HttpServletResponse response, String name, String value, int maxAge) {
        response.addHeader("Set-Cookie",
            String.format("%s=%s; Path=/; Max-Age=%d; HttpOnly; SameSite=Lax", name, value, maxAge));
    }

    private void clearCookie(HttpServletResponse response, String name) {
        response.addHeader("Set-Cookie",
            String.format("%s=; Path=/; Max-Age=0; HttpOnly; SameSite=Lax", name));
    }

    private String getCookieValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
