package web.blog.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.sql.Timestamp;
import web.blog.service.ArticleService;
import web.blog.service.CookieService;
import web.blog.service.JwtTokenService;
import web.blog.service.impl.ArticleServiceImpl;
import web.blog.service.impl.CookieServiceImpl;
import web.blog.service.impl.JwtTokenServiceImpl;
import web.blog.util.RedisRateLimiter;
import web.blog.dto.ArticleCreateRequest;
import web.blog.dto.ArticleListResponse;
import web.blog.dto.ArticleAdminResponse;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebServlet("/api/admin/articles")
public class ArticleAdminListController extends HttpServlet {
    private ArticleService articleService;
    private JwtTokenService jwtTokenService;
    private CookieService cookieService;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        try {
            articleService = new ArticleServiceImpl();
            jwtTokenService = new JwtTokenServiceImpl();
            cookieService = new CookieServiceImpl();
        } catch (NamingException e) {
            throw new ServletException("Failed to initialize ArticleAdminListController", e);
        }
        gson = new GsonBuilder()
            .registerTypeAdapter(Timestamp.class, new web.blog.util.UtcTimestampAdapter())
            .create();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        String authorSlug = validateAndGetAuthorSlug(req, resp);
        if (authorSlug == null) return;

        int page = req.getParameter("page") != null ? Integer.parseInt(req.getParameter("page")) : 0;
        int size = req.getParameter("size") != null ? Integer.parseInt(req.getParameter("size")) : 10;

        ArticleListResponse<ArticleAdminResponse> data = articleService.listArticlesAdmin(authorSlug, page, size);
        Map<String, Object> response = Map.of("success", true, "data", data);
        resp.getWriter().write(gson.toJson(response));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        String authorSlug = validateAndGetAuthorSlug(req, resp);
        if (authorSlug == null) return;

        if (!RedisRateLimiter.isAllowed(authorSlug, "create",
                RedisRateLimiter.ARTICLE_CREATE_LIMIT, RedisRateLimiter.WINDOW_SECONDS)) {
            resp.setStatus(429);
            Map<String, Object> response = Map.of("success", false, "errMsg", "創建次數過多，請稍後再試");
            resp.getWriter().write(gson.toJson(response));
            return;
        }

        ArticleCreateRequest request = gson.fromJson(req.getReader(), ArticleCreateRequest.class);
        ArticleAdminResponse data = articleService.createArticle(request, authorSlug);

        if (data != null) {
            resp.setStatus(201);
            Map<String, Object> response = Map.of("success", true, "data", data);
            resp.getWriter().write(gson.toJson(response));
        } else {
            resp.setStatus(400);
            Map<String, Object> response = Map.of("success", false, "errMsg", "建立文章失敗，請檢查輸入資料");
            resp.getWriter().write(gson.toJson(response));
        }
    }

    private String validateAndGetAuthorSlug(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String accessToken = cookieService.getAccessToken(req);
        if (accessToken == null) {
            resp.setStatus(401);
            Map<String, Object> response = Map.of("success", false, "errMsg", "未提供存取權杖");
            resp.getWriter().write(gson.toJson(response));
            return null;
        }

        DecodedJWT decoded = jwtTokenService.validateAccessToken(accessToken);
        if (decoded == null) {
            resp.setStatus(401);
            Map<String, Object> response = Map.of("success", false, "errMsg", "無效的存取權杖");
            resp.getWriter().write(gson.toJson(response));
            return null;
        }
        return jwtTokenService.getAuthorSlug(decoded);
    }
}
