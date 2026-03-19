package web.checkout.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import web.checkout.service.CallbackService;

@WebServlet("/checkout/ecpay/callback")
public class CallbackServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

    private CallbackService callbackService;
    
    @Override
    public void init() {
        ApplicationContext applicationContext =
            WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        callbackService = applicationContext.getBean(CallbackService.class);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // 把 callback 參數轉成 Map<String,String>
        Map<String, String> params = new java.util.HashMap<>();

        for (Map.Entry<String, String[]> e : req.getParameterMap().entrySet()) {
            String key = e.getKey();
            String val = (e.getValue() != null && e.getValue().length > 0) ? e.getValue()[0] : "";

            params.put(key, val);

            // 先印出看看 ECPay 回傳哪些值
            System.out.println(key + "=" + val);
        }

        // 呼叫 Service 處理 callback
        String reply = callbackService.handleCallback(params);

        // 回覆 ECPay
        resp.setContentType("text/plain; charset=UTF-8");
        resp.getWriter().write(reply);
        
    }
}
