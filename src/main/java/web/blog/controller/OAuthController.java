package web.blog.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import web.blog.service.BloggerService;
import web.blog.service.CookieService;
import web.blog.service.GoogleOAuthService;
import web.blog.service.JwtTokenService;
import web.blog.service.impl.BloggerServiceImpl;
import web.blog.service.impl.CookieServiceImpl;
import web.blog.service.impl.GoogleOAuthServiceImpl;
import web.blog.service.impl.JwtTokenServiceImpl;
import web.blog.vo.Blogger;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@WebServlet("/api/oauth/*")
public class OAuthController extends HttpServlet {
    private GoogleOAuthService googleOAuthService;
    private JwtTokenService jwtTokenService;
    private CookieService cookieService;
    private BloggerService bloggerService;

    @Override
    public void init() throws ServletException {
        try {
            googleOAuthService = new GoogleOAuthServiceImpl();
            jwtTokenService = new JwtTokenServiceImpl();
            cookieService = new CookieServiceImpl();
            bloggerService = new BloggerServiceImpl();
        } catch (NamingException e) {
            throw new ServletException("Failed to initialize OAuthController", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) {
            resp.sendError(404);
            return;
        }

        switch (pathInfo) {
            case "/google/login":
                handleGoogleLogin(req, resp);
                break;
            case "/google/callback":
                handleGoogleCallback(req, resp);
                break;
            default:
                resp.sendError(404);
        }
    }

    private void handleGoogleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String returnUrl = req.getParameter("returnUrl");
        if (returnUrl != null) {
            cookieService.setReturnUrlCookie(resp, returnUrl);
        }
        
        String state = UUID.randomUUID().toString();
        cookieService.setStateCookie(resp, state);
        String authUrl = googleOAuthService.getAuthorizationUrl(state);
        resp.sendRedirect(authUrl);
    }

    private void handleGoogleCallback(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String code = req.getParameter("code");
        String state = req.getParameter("state");

        String savedState = getCookieValue(req, "oauth_state");
        if (savedState == null || !savedState.equals(state)) {
            resp.sendRedirect("http://localhost:8080/blog-login.html?error=invalid_state");
            return;
        }
        cookieService.clearStateCookie(resp);

        try {
            Map<String, String> tokens = googleOAuthService.exchangeCodeForTokens(code);
            GoogleIdToken.Payload payload = googleOAuthService.verifyIdToken(tokens.get("id_token"));

            String googleSub = payload.getSubject();
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String picture = (String) payload.get("picture");

            Blogger blogger = bloggerService.findByGoogleSub(googleSub);
            if (blogger == null) {
                blogger = bloggerService.findByEmail(email);
                if (blogger != null) {
                    blogger.setGoogleSub(googleSub);
                    blogger.setGoogleEmail(email);
                    bloggerService.updateFromOAuth(blogger, name, picture);
                } else {
                    blogger = bloggerService.createFromOAuth(googleSub, email, name, picture);
                }
            } else {
                bloggerService.updateFromOAuth(blogger, name, picture);
            }

            String accessToken = jwtTokenService.createAccessToken(blogger);
            String refreshToken = jwtTokenService.createRefreshToken(blogger);

            cookieService.setAccessTokenCookie(resp, accessToken);
            cookieService.setRefreshTokenCookie(resp, refreshToken);

            String returnUrl = getCookieValue(req, "return_url");
            cookieService.deleteReturnUrlCookie(resp);
            
            resp.sendRedirect("http://localhost:8080" + (returnUrl != null ? returnUrl : "/blog-admin.html"));

        } catch (Exception e) {
            System.err.println("OAuth callback failed: " + e.getMessage());
            resp.sendRedirect("http://localhost:8080/blog-login.html?error=auth_failed");
        }
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
