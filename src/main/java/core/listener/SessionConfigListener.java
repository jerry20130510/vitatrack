package core.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.SessionCookieConfig;
import javax.servlet.annotation.WebListener;

import core.util.HibernateUtil;

@WebListener
public class SessionConfigListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        SessionCookieConfig config = sce.getServletContext().getSessionCookieConfig();
        config.setHttpOnly(true);
        config.setSecure(false);
        config.setMaxAge(1800);
        HibernateUtil.getSessionFactory();
    }
    

	 @Override
	 public void contextDestroyed(ServletContextEvent sce) {
	  HibernateUtil.shutdown();
	  
	 }
}

