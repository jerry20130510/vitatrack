package web.blog.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import web.blog.vo.ArticleListResponse;
import web.blog.service.ArticleService;
import web.blog.service.impl.ArticleServiceImpl;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebServlet("/api/articles")
public class ArticleListController extends HttpServlet {
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
            int page = req.getParameter("page") != null ? Integer.parseInt(req.getParameter("page")) : 0;
            int size = req.getParameter("size") != null ? Integer.parseInt(req.getParameter("size")) : 6;
            String category = req.getParameter("category");
            String authorSlug = req.getParameter("authorSlug");
            
            if (page < 0 || size <= 0) {
                throw new NumberFormatException("Invalid page or size parameter");
            }
            
            ArticleListResponse data = articleService.listArticles(page, size, category, authorSlug);
            
            Map<String, Object> response = Map.of("success", true, "data", data);
            resp.getWriter().write(gson.toJson(response));
            
        } catch (NumberFormatException e) {
            resp.setStatus(400);
            Map<String, Object> response = Map.of("success", false, "errMsg", "Bad Request");
            resp.getWriter().write(gson.toJson(response));
            
        } catch (RuntimeException e) {
            resp.setStatus(500);
            Map<String, Object> response = Map.of("success", false, "errMsg", "Server Error");
            resp.getWriter().write(gson.toJson(response));
        }
    }
}
