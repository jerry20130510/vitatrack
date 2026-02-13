package web.blog.controller;

import com.google.gson.Gson;
import web.blog.service.ArticleService;
import web.blog.service.impl.ArticleServiceImpl;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebServlet("/api/articles/like")
public class ArticleLikeController extends HttpServlet {
    private ArticleService articleService;
    private Gson gson;

    @Override
    public void init() {
        articleService = new ArticleServiceImpl();
        gson = new Gson();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        String slug = req.getParameter("slug");
        
        if (slug == null || slug.trim().isEmpty()) {
            resp.setStatus(400);
            Map<String, Object> response = Map.of("success", false, "errMsg", "缺少slug參數");
            resp.getWriter().write(gson.toJson(response));
            return;
        }
        
        String errMsg = articleService.incrementLikes(slug);
        if (errMsg != null) {
            resp.setStatus(404);
            Map<String, Object> response = Map.of("success", false, "errMsg", errMsg);
            resp.getWriter().write(gson.toJson(response));
            return;
        }
        
        int likes = articleService.getLikes(slug);
        Map<String, Object> response = Map.of(
            "success", true,
            "data", Map.of("totalLikes", likes)
        );
        resp.getWriter().write(gson.toJson(response));
    }
}
