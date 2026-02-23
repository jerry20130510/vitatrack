package web.blog.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.sql.Timestamp;
import web.blog.service.CookieService;
import web.blog.service.DashboardService;
import web.blog.service.JwtTokenService;
import web.blog.service.impl.CookieServiceImpl;
import web.blog.service.impl.DashboardServiceImpl;
import web.blog.service.impl.JwtTokenServiceImpl;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebServlet("/api/admin/dashboard")
public class DashboardController extends HttpServlet {
    private JwtTokenService jwtTokenService;
    private CookieService cookieService;
    private DashboardService dashboardService;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        try {
            jwtTokenService = new JwtTokenServiceImpl();
            cookieService = new CookieServiceImpl();
            dashboardService = new DashboardServiceImpl();
        } catch (NamingException e) {
            throw new ServletException("Failed to initialize DashboardController", e);
        }
        gson = new GsonBuilder()
            .registerTypeAdapter(Timestamp.class, new web.blog.util.UtcTimestampAdapter())
            .create();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");

        String accessToken = cookieService.getAccessToken(req);
        if (accessToken == null) {
            resp.setStatus(401);
            Map<String, Object> response = Map.of("success", false, "errMsg", "未提供存取權杖");
            resp.getWriter().write(gson.toJson(response));
            return;
        }

        DecodedJWT decoded = jwtTokenService.validateAccessToken(accessToken);
        if (decoded == null) {
            resp.setStatus(401);
            Map<String, Object> response = Map.of("success", false, "errMsg", "無效的存取權杖");
            resp.getWriter().write(gson.toJson(response));
            return;
        }
        String authorSlug = jwtTokenService.getAuthorSlug(decoded);

        Map<String, Object> data = dashboardService.getDashboardData(authorSlug);

        if (data != null) {
            Map<String, Object> response = Map.of("success", true, "data", data);
            resp.getWriter().write(gson.toJson(response));
        } else {
            resp.setStatus(500);
            Map<String, Object> response = Map.of("success", false, "errMsg", "無法取得儀表板資料");
            resp.getWriter().write(gson.toJson(response));
        }
    }
}
