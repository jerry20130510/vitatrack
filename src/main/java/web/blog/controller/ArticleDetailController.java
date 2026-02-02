package web.blog.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import web.blog.bean.Article;
import web.blog.service.ArticleService;
import web.blog.service.impl.ArticleServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/api/articles/*")
public class ArticleDetailController extends HttpServlet {
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
        
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            JsonObject response = new JsonObject();
            response.addProperty("success", false);
            response.addProperty("errMsg", "缺少文章slug");
            resp.setContentType("application/json; charset=UTF-8");
            resp.getWriter().write(gson.toJson(response));
            return;
        }
        
        Article data = articleService.getArticleDetail(pathInfo.substring(1));
        
        JsonObject response = new JsonObject();
        if (data != null) {
            response.addProperty("success", true);
            
            JsonElement dataJson = gson.toJsonTree(data);
            response.add("data", dataJson);
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.addProperty("success", false);
            response.addProperty("errMsg", "文章不存在");
        }
        
        resp.setContentType("application/json; charset=UTF-8");
        resp.getWriter().write(gson.toJson(response));
    }
}
