package web.product.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import web.product.service.ProductService;
import web.product.service.impl.ProductServiceImpl;
import web.product.vo.Product;


@WebServlet("/product-add")
public class ProductController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ProductService productService;
	
	@Override
	public void init() throws ServletException {
		productService = new ProductServiceImpl();
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        Product product = gson.fromJson(req.getReader(), Product.class);

        boolean success = productService.add(product);

<<<<<<< HEAD
	        try (BufferedReader reader = req.getReader()) {
	            Product product = gson.fromJson(req.getReader(), Product.class);
	            boolean ok = productService.create(product);

	            if (ok) {
	                resp.setStatus(HttpServletResponse.SC_CREATED);
	                resp.getWriter().write("新建成功");
	            } else {
	                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	                resp.getWriter().write("新建失敗");
	            }
	        } catch (Exception e) {
	            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	            resp.getWriter().write("資料庫連線失敗");
	        }
	    }
	    
	    @Override
	    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	        resp.setContentType("application/json; charset=UTF-8");

	        try {
	            String sku = extractSkuFromPath(req);
	            if (sku == null || sku.trim().isEmpty()) {
	                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	                resp.getWriter().write("{\"success\":false}");
	                return;
	            }

	            String body = readBody(req);
	            Product product = gson.fromJson(body, Product.class);

	            boolean ok = productService.update(sku, product);

	            if (ok) {
	                resp.setStatus(HttpServletResponse.SC_OK);
	                resp.getWriter().write("{\"success\":true}");
	            } else {
	                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	                resp.getWriter().write("{\"success\":false}");
	            }

	        } catch (Exception e) {
	            e.printStackTrace();
	            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	            resp.getWriter().write("{\"success\":false}");
	        }
	    }

	    // ====== 讀 body（給 doPut 用）======
	    private String readBody(HttpServletRequest req) throws IOException {
	        try (BufferedReader reader = req.getReader()) {
	            return reader.lines().collect(Collectors.joining());
	        }
	    }

	    // ====== 從 /admin/products/{sku} 取出 sku ======
	    private String extractSkuFromPath(HttpServletRequest req) {
	        String pathInfo = req.getPathInfo();
	        if (pathInfo == null) return null;

	        pathInfo = pathInfo.trim();
	        if (pathInfo.isEmpty() || "/".equals(pathInfo)) return null;

	        if (pathInfo.startsWith("/")) pathInfo = pathInfo.substring(1);

	        int slashIdx = pathInfo.indexOf('/');
	        if (slashIdx >= 0) {
	            pathInfo = pathInfo.substring(0, slashIdx);
	        }
	        return pathInfo;
	    }
		
	};
=======
        resp.getWriter().write(gson.toJson(success));
	}

}
>>>>>>> main
