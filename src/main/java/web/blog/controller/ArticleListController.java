package web.blog.controller;

import com.google.gson.Gson;
import javax.naming.NamingException;
import com.google.gson.GsonBuilder;
import javax.naming.NamingException;
import web.blog.vo.ArticleListResponse;
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

@WebServlet("/api/articles")
public class ArticleListController extends HttpServlet {
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
        
        int page = req.getParameter("page") != null ? Integer.parseInt(req.getParameter("page")) : 0;
        int size = req.getParameter("size") != null ? Integer.parseInt(req.getParameter("size")) : 6;
        String category = req.getParameter("category");
        String authorSlug = req.getParameter("authorSlug");
        
        ArticleListResponse data = articleService.listArticles(page, size, category, authorSlug);
        
        Map<String, Object> response = Map.of("success", true, "data", data);
        resp.getWriter().write(gson.toJson(response));
    }
}
