package core.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {
	@Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
		
		if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
		    return true; 
		}

        HttpSession session = request.getSession(false);

        Object member = (session != null) ? session.getAttribute("member") : null;

        // 未登入
        if (member == null) {

            // 回 HTTP 狀態碼（依據 REST API 慣例）
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"NOT_LOGIN\"}");

            return false; //中斷Controller執行
        }

        return true; //放行
    }
}
