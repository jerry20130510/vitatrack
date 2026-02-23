package web.blog.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.sql.Timestamp;
import web.blog.service.AuthService;
import web.blog.service.BloggerService;
import web.blog.service.CookieService;
import web.blog.service.JwtTokenService;
import web.blog.service.impl.AuthServiceImpl;
import web.blog.service.impl.BloggerServiceImpl;
import web.blog.service.impl.CookieServiceImpl;
import web.blog.service.impl.JwtTokenServiceImpl;
import web.blog.vo.Blogger;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebServlet("/api/token/*")
public class TokenController extends HttpServlet {
    private JwtTokenService jwtTokenService;
    private AuthService authService;
    private CookieService cookieService;
    private BloggerService bloggerService;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        try {
            jwtTokenService = new JwtTokenServiceImpl();
            authService = new AuthServiceImpl();
            cookieService = new CookieServiceImpl();
            bloggerService = new BloggerServiceImpl();
        } catch (NamingException e) {
            throw new ServletException("Failed to initialize TokenController", e);
        }
        gson = new GsonBuilder()
            .registerTypeAdapter(Timestamp.class, new web.blog.util.UtcTimestampAdapter())
            .create();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if ("/status".equals(pathInfo)) {
            handleStatus(req, resp);
        } else {
            resp.sendError(404);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) {
            resp.sendError(404);
            return;
        }

        switch (pathInfo) {
            case "/refresh": handleRefresh(req, resp); break;
            case "/logout": handleLogout(req, resp); break;
            default: resp.sendError(404);
        }
    }

    private void handleStatus(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        String accessToken = cookieService.getAccessToken(req);
        Map<String, Object> data = authService.getAuthStatus(accessToken);

        if (!(Boolean) data.get("authenticated")) {
            resp.setStatus(401);
        }

        Map<String, Object> response = Map.of("success", true, "data", data);
        resp.getWriter().write(gson.toJson(response));
    }

    private void handleRefresh(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        String refreshToken = cookieService.getRefreshToken(req);

        if (refreshToken == null) {
            resp.setStatus(401);
            Map<String, Object> response = Map.of("success", false, "errMsg", "No refresh token found");
            resp.getWriter().write(gson.toJson(response));
            return;
        }

        DecodedJWT decoded = jwtTokenService.validateRefreshToken(refreshToken);
        if (decoded == null) {
            resp.setStatus(401);
            Map<String, Object> response = Map.of("success", false, "errMsg", "Invalid refresh token");
            resp.getWriter().write(gson.toJson(response));
            return;
        }

        String email = decoded.getSubject();
        Blogger blogger = bloggerService.findByEmail(email);

        if (blogger == null) {
            resp.setStatus(401);
            Map<String, Object> response = Map.of("success", false, "errMsg", "Blogger not found");
            resp.getWriter().write(gson.toJson(response));
        } else {
            String newAccessToken = jwtTokenService.createAccessToken(blogger);
            cookieService.setAccessTokenCookie(resp, newAccessToken);
            Map<String, Object> response = Map.of("success", true, "message", "Token refreshed successfully");
            resp.getWriter().write(gson.toJson(response));
        }
    }

    private void handleLogout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        cookieService.clearAuthCookies(resp);
        Map<String, Object> response = Map.of("success", true, "message", "Logged out successfully");
        resp.getWriter().write(gson.toJson(response));
    }
}
