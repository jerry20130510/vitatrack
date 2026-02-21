package web.blog.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.sql.Timestamp;
import web.blog.service.CookieService;
import web.blog.service.JwtTokenService;
import web.blog.service.S3PresignedUrlService;
import web.blog.service.impl.CookieServiceImpl;
import web.blog.service.impl.JwtTokenServiceImpl;
import web.blog.service.impl.S3PresignedUrlServiceImpl;
import web.blog.util.RedisRateLimiter;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/api/admin/images/*")
public class ImageAdminController extends HttpServlet {
    private S3PresignedUrlService s3PresignedUrlService;
    private JwtTokenService jwtTokenService;
    private CookieService cookieService;
    private Gson gson;
    private static final List<String> ALLOWED_TYPES = List.of("image/jpeg", "image/png");

    @Override
    public void init() throws ServletException {
        try {
            s3PresignedUrlService = new S3PresignedUrlServiceImpl();
            jwtTokenService = new JwtTokenServiceImpl();
            cookieService = new CookieServiceImpl();
        } catch (NamingException e) {
            throw new ServletException("Failed to initialize ImageAdminController", e);
        }
        gson = new GsonBuilder()
            .registerTypeAdapter(Timestamp.class, new web.blog.util.UtcTimestampAdapter())
            .create();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        String pathInfo = req.getPathInfo();

        if ("/presign".equals(pathInfo)) {
            handlePresign(req, resp);
        } else {
            resp.sendError(404);
        }
    }

    private void handlePresign(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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

        if (!RedisRateLimiter.isAllowed(authorSlug, "upload",
                RedisRateLimiter.IMAGE_UPLOAD_LIMIT, RedisRateLimiter.WINDOW_SECONDS)) {
            resp.setStatus(429);
            Map<String, Object> response = Map.of("success", false, "errMsg", "上傳次數過多，請稍後再試");
            resp.getWriter().write(gson.toJson(response));
            return;
        }

        Map<String, Object> request = gson.fromJson(req.getReader(), Map.class);
        String fileName = (String) request.get("fileName");
        String contentType = (String) request.get("contentType");
        String articleSlug = (String) request.get("articleSlug");

        if (fileName == null || contentType == null || articleSlug == null) {
            resp.setStatus(400);
            Map<String, Object> response = Map.of("success", false, "errMsg", "缺少必要參數");
            resp.getWriter().write(gson.toJson(response));
            return;
        }

        if (!ALLOWED_TYPES.contains(contentType)) {
            resp.setStatus(400);
            Map<String, Object> response = Map.of("success", false, "errMsg", "檔案類型不支援，僅支援JPEG和PNG");
            resp.getWriter().write(gson.toJson(response));
            return;
        }

        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
        Map<String, Object> data = s3PresignedUrlService.generatePresignedUrl(articleSlug, fileExtension);

        Map<String, Object> response = Map.of("success", true, "data", data);
        resp.getWriter().write(gson.toJson(response));
    }
}
