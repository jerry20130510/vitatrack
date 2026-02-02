package web.blog.controller;

import web.blog.service.ArticleService;
import web.blog.service.impl.ArticleServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/api/articles/like")
public class ArticleLikeController extends HttpServlet {
    private ArticleService articleService;

    @Override
    public void init() throws ServletException {
        articleService = new ArticleServiceImpl();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        String slug = req.getParameter("slug");
        
        if (slug == null || slug.trim().isEmpty()) {
            resp.getWriter().write("{\"success\":false,\"errMsg\":\"缺少slug參數\"}");
            return;
        }
        
        String errMsg = articleService.incrementLikes(slug);
        if (errMsg != null) {
            resp.getWriter().write("{\"success\":false,\"errMsg\":\"" + errMsg + "\"}");
            return;
        }
        
        int likes = articleService.getLikes(slug);
        resp.getWriter().write("{\"success\":true,\"data\":{\"totalLikes\":" + likes + "}}");
    }
}
