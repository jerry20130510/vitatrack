package web.blog.controller;

import com.google.gson.*;
import web.blog.bean.ArticleListResponse;
import web.blog.service.ArticleService;
import web.blog.service.impl.ArticleServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/api/articles")
public class ArticleListController extends HttpServlet {
    private ArticleService articleService;

    @Override
    public void init() throws ServletException {
        articleService = new ArticleServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .create();
        
        int page = req.getParameter("page") != null ? Integer.parseInt(req.getParameter("page")) : 0;
        int size = req.getParameter("size") != null ? Integer.parseInt(req.getParameter("size")) : 6;
        String category = req.getParameter("category");
        String authorSlug = req.getParameter("authorSlug");
        
        ArticleListResponse data = articleService.listArticles(page, size, category, authorSlug);
        
        JsonObject response = new JsonObject();
        response.addProperty("success", true);
        
        JsonElement dataJson = gson.toJsonTree(data);
        response.add("data", dataJson);
        
        resp.setContentType("application/json; charset=UTF-8");
        resp.getWriter().write(gson.toJson(response));
    }
}
