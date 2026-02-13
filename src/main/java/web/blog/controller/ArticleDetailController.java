package web.blog.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import web.blog.vo.Article;
import web.blog.service.ArticleService;
import web.blog.service.impl.ArticleServiceImpl;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebServlet("/api/articles/*")
public class ArticleDetailController extends HttpServlet {
    private ArticleService articleService;
    private Gson gson;

    @Override
    public void init() {
        articleService = new ArticleServiceImpl();
        gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .create();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        
        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                resp.setStatus(400);
                Map<String, Object> response = Map.of("success", false, "errMsg", "Missing article slug");
                resp.getWriter().write(gson.toJson(response));
                return;
            }
            
            Article data = articleService.getArticleDetail(pathInfo.substring(1));
            
            if (data != null) {
                Map<String, Object> response = Map.of("success", true, "data", data);
                resp.getWriter().write(gson.toJson(response));
            } else {
                resp.setStatus(404);
                Map<String, Object> response = Map.of("success", false, "errMsg", "Article not found");
                resp.getWriter().write(gson.toJson(response));
            }
            
        } catch (RuntimeException e) {
            resp.setStatus(500);
            Map<String, Object> response = Map.of("success", false, "errMsg", "Server Error");
            resp.getWriter().write(gson.toJson(response));
        }
    }
}
