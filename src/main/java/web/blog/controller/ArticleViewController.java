package web.blog.controller;

import com.google.gson.Gson;
import web.blog.service.ArticleService;
import web.blog.service.impl.ArticleServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.naming.NamingException;
import java.io.IOException;
import java.util.Map;

@WebServlet("/api/articles/view")
public class ArticleViewController extends HttpServlet {
    private ArticleService articleService;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        try {
            articleService = new ArticleServiceImpl();
        } catch (NamingException e) {
            throw new ServletException("Failed to initialize ArticleService", e);
        }
        gson = new Gson();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        String slug = req.getParameter("slug");
        
        if (slug == null || slug.trim().isEmpty()) {
            Map<String, Object> response = Map.of("success", false, "errMsg", "缺少slug參數");
            resp.getWriter().write(gson.toJson(response));
            return;
        }
        
        String errMsg = articleService.incrementViews(slug);
        if (errMsg != null) {
            Map<String, Object> response = Map.of("success", false, "errMsg", errMsg);
            resp.getWriter().write(gson.toJson(response));
            return;
        }
        
        int views = articleService.getViews(slug);
        Map<String, Object> response = Map.of(
            "success", true,
            "data", Map.of("totalViews", views)
        );
        resp.getWriter().write(gson.toJson(response));
    }
}
