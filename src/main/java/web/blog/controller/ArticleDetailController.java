package web.blog.controller;

import com.google.gson.Gson;
import javax.naming.NamingException;
import com.google.gson.GsonBuilder;
import javax.naming.NamingException;
import web.blog.vo.Article;
import javax.naming.NamingException;
import web.blog.service.ArticleService;
import javax.naming.NamingException;
import web.blog.service.impl.ArticleServiceImpl;
import javax.naming.NamingException;

import javax.servlet.ServletException;
import javax.naming.NamingException;
import javax.servlet.annotation.WebServlet;
import javax.naming.NamingException;
import javax.servlet.http.HttpServlet;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletResponse;
import javax.naming.NamingException;
import java.io.IOException;
import javax.naming.NamingException;
import java.util.Map;
import javax.naming.NamingException;

@WebServlet("/api/articles/*")
public class ArticleDetailController extends HttpServlet {
    private ArticleService articleService;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        try {
            articleService = new ArticleServiceImpl();
        } catch (NamingException e) {
            throw new ServletException("Failed to initialize ArticleService", e);
        }
        gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .create();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            Map<String, Object> response = Map.of("success", false, "errMsg", "缺少文章slug");
            resp.getWriter().write(gson.toJson(response));
            return;
        }
        
        Article data = articleService.getArticleDetail(pathInfo.substring(1));
        
        if (data != null) {
            Map<String, Object> response = Map.of("success", true, "data", data);
            resp.getWriter().write(gson.toJson(response));
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            Map<String, Object> response = Map.of("success", false, "errMsg", "文章不存在");
            resp.getWriter().write(gson.toJson(response));
        }
    }
}
