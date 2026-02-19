package web.blog.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.sql.Timestamp;
import web.blog.service.ArticleEngagementService;
import web.blog.service.ArticleService;
import web.blog.service.impl.ArticleEngagementServiceImpl;
import web.blog.service.impl.ArticleServiceImpl;
import web.blog.vo.Article;
import web.blog.dto.ArticleListResponse;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebServlet("/api/articles/*")
public class ArticlePublicController extends HttpServlet {
    private ArticleService articleService;
    private ArticleEngagementService engagementService;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        try {
            articleService = new ArticleServiceImpl();
            engagementService = new ArticleEngagementServiceImpl();
        } catch (NamingException e) {
            throw new ServletException("Failed to initialize ArticlePublicController", e);
        }
        gson = new GsonBuilder()
            .registerTypeAdapter(Timestamp.class, new web.blog.util.UtcTimestampAdapter())
            .create();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            handleList(req, resp);
        } else {
            handleDetail(resp, pathInfo.substring(1));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        String pathInfo = req.getPathInfo();

        if (pathInfo == null) {
            resp.setStatus(404);
            return;
        }

        String[] parts = pathInfo.substring(1).split("/");
        if (parts.length != 2) {
            resp.setStatus(404);
            return;
        }

        String slug = parts[0];
        String action = parts[1];

        switch (action) {
            case "views": handleView(resp, slug); break;
            case "likes": handleLike(resp, slug); break;
            case "shares": handleShare(resp, slug); break;
            default: resp.setStatus(404);
        }
    }

    private void handleList(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int page = req.getParameter("page") != null ? Integer.parseInt(req.getParameter("page")) : 0;
        int size = req.getParameter("size") != null ? Integer.parseInt(req.getParameter("size")) : 6;
        String category = req.getParameter("category");
        String authorSlug = req.getParameter("authorSlug");

        ArticleListResponse data = articleService.listArticles(page, size, category, authorSlug);
        Map<String, Object> response = Map.of("success", true, "data", data);
        resp.getWriter().write(gson.toJson(response));
    }

    private void handleDetail(HttpServletResponse resp, String slug) throws IOException {
        Article data = articleService.getArticleDetail(slug);

        if (data != null) {
            Map<String, Object> response = Map.of("success", true, "data", data);
            resp.getWriter().write(gson.toJson(response));
        } else {
            resp.setStatus(404);
            Map<String, Object> response = Map.of("success", false, "errMsg", "文章不存在");
            resp.getWriter().write(gson.toJson(response));
        }
    }

    private void handleView(HttpServletResponse resp, String slug) throws IOException {
        String errMsg = engagementService.incrementViews(slug);
        if (errMsg != null) {
            resp.setStatus(404);
            Map<String, Object> response = Map.of("success", false, "errMsg", errMsg);
            resp.getWriter().write(gson.toJson(response));
            return;
        }
        resp.getWriter().write(gson.toJson(Map.of("success", true)));
    }

    private void handleLike(HttpServletResponse resp, String slug) throws IOException {
        String errMsg = engagementService.incrementLikes(slug);
        if (errMsg != null) {
            resp.setStatus(404);
            Map<String, Object> response = Map.of("success", false, "errMsg", errMsg);
            resp.getWriter().write(gson.toJson(response));
            return;
        }

        int likes = engagementService.getLikes(slug);
        Map<String, Object> response = Map.of(
            "success", true,
            "data", Map.of("totalLikes", likes)
        );
        resp.getWriter().write(gson.toJson(response));
    }

    private void handleShare(HttpServletResponse resp, String slug) throws IOException {
        String errMsg = engagementService.incrementShares(slug);
        if (errMsg != null) {
            resp.setStatus(404);
            Map<String, Object> response = Map.of("success", false, "errMsg", errMsg);
            resp.getWriter().write(gson.toJson(response));
            return;
        }

        int shares = engagementService.getShares(slug);
        Map<String, Object> response = Map.of(
            "success", true,
            "data", Map.of("totalShares", shares)
        );
        resp.getWriter().write(gson.toJson(response));
    }
}
