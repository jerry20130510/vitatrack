package web.blog.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.sql.Timestamp;
import web.blog.dto.ArticleCreateRequest;
import web.blog.dto.ArticleUpdateRequest;
import web.blog.dto.ArticleAdminResponse;
import web.blog.service.ArticleService;
import web.blog.service.CookieService;
import web.blog.service.JwtTokenService;
import web.blog.service.impl.ArticleServiceImpl;
import web.blog.service.impl.CookieServiceImpl;
import web.blog.service.impl.JwtTokenServiceImpl;
import web.blog.util.RedisRateLimiter;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebServlet("/api/admin/articles/*")
public class ArticleAdminController extends HttpServlet {
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
            throw new ServletException("Failed to initialize ArticleAdminController", e);
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

        handleGetDetail(req, resp, authorSlug);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        String authorSlug = validateAndGetAuthorSlug(req, resp);
        if (authorSlug == null) return;

        handleCreate(req, resp, authorSlug);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        String authorSlug = validateAndGetAuthorSlug(req, resp);
        if (authorSlug == null) return;

        handleUpdate(req, resp, authorSlug);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        String authorSlug = validateAndGetAuthorSlug(req, resp);
        if (authorSlug == null) return;

        handleDelete(req, resp, authorSlug);
    }

    private void handleGetDetail(HttpServletRequest req, HttpServletResponse resp, String authorSlug) throws IOException {
        Long id = parseArticleId(req.getPathInfo(), resp);
        if (id == null) return;

        ArticleAdminResponse data = articleService.getArticleDetailAdmin(id, authorSlug);

        if (data != null) {
            Map<String, Object> response = Map.of("success", true, "data", data);
            resp.getWriter().write(gson.toJson(response));
        } else {
            resp.setStatus(404);
            Map<String, Object> response = Map.of("success", false, "errMsg", "文章不存在或無權限查看");
            resp.getWriter().write(gson.toJson(response));
        }
    }

    private void handleCreate(HttpServletRequest req, HttpServletResponse resp, String authorSlug) throws IOException {
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
            Map<String, Object> response = Map.of("success", true, "data", data);
            resp.getWriter().write(gson.toJson(response));
        } else {
            resp.setStatus(400);
            Map<String, Object> response = Map.of("success", false, "errMsg", "建立文章失敗，請檢查輸入資料");
            resp.getWriter().write(gson.toJson(response));
        }
    }

    private void handleUpdate(HttpServletRequest req, HttpServletResponse resp, String authorSlug) throws IOException {
        if (!RedisRateLimiter.isAllowed(authorSlug, "update",
                RedisRateLimiter.ARTICLE_UPDATE_LIMIT, RedisRateLimiter.WINDOW_SECONDS)) {
            resp.setStatus(429);
            Map<String, Object> response = Map.of("success", false, "errMsg", "更新次數過多，請稍後再試");
            resp.getWriter().write(gson.toJson(response));
            return;
        }

        Long id = parseArticleId(req.getPathInfo(), resp);
        if (id == null) return;

        ArticleUpdateRequest request = gson.fromJson(req.getReader(), ArticleUpdateRequest.class);
        ArticleAdminResponse data = articleService.updateArticle(id, request, authorSlug);

        if (data != null) {
            Map<String, Object> response = Map.of("success", true, "data", data);
            resp.getWriter().write(gson.toJson(response));
        } else {
            resp.setStatus(409);
            Map<String, Object> response = Map.of(
                "success", false,
                "errMsg", "更新失敗，文章不存在、無權限或已被其他人修改",
                "conflictType", "version_mismatch"
            );
            resp.getWriter().write(gson.toJson(response));
        }
    }

    private void handleDelete(HttpServletRequest req, HttpServletResponse resp, String authorSlug) throws IOException {
        if (!RedisRateLimiter.isAllowed(authorSlug, "delete",
                RedisRateLimiter.ARTICLE_DELETE_LIMIT, RedisRateLimiter.WINDOW_SECONDS)) {
            resp.setStatus(429);
            Map<String, Object> response = Map.of("success", false, "errMsg", "刪除次數過多，請稍後再試");
            resp.getWriter().write(gson.toJson(response));
            return;
        }

        Long id = parseArticleId(req.getPathInfo(), resp);
        if (id == null) return;

        String errMsg = articleService.deleteArticle(id, authorSlug);

        if (errMsg == null) {
            Map<String, Object> response = Map.of("success", true, "message", "文章刪除成功");
            resp.getWriter().write(gson.toJson(response));
        } else {
            resp.setStatus(400);
            Map<String, Object> response = Map.of("success", false, "errMsg", errMsg);
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

    private Long parseArticleId(String pathInfo, HttpServletResponse resp) throws IOException {
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(400);
            Map<String, Object> response = Map.of("success", false, "errMsg", "缺少文章ID");
            resp.getWriter().write(gson.toJson(response));
            return null;
        }

        String idStr = pathInfo.substring(1);
        try {
            return Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            resp.setStatus(400);
            Map<String, Object> response = Map.of("success", false, "errMsg", "無效的文章ID");
            resp.getWriter().write(gson.toJson(response));
            return null;
        }
    }
}
