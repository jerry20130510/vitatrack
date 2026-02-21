package web.blog.listener;

import web.blog.service.S3PresignedUrlService;
import web.blog.service.impl.S3PresignedUrlServiceImpl;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class S3LifecycleConfigListener implements ServletContextListener {
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("[S3 Lifecycle] Skipping S3 lifecycle configuration (configure manually if needed)");
        // Temporarily disabled to allow app to start with parameterized config
        // Uncomment below to enable automatic S3 lifecycle policy configuration
        /*
        try {
            S3PresignedUrlService s3Service = new S3PresignedUrlServiceImpl();
            s3Service.configureLifecyclePolicy();
            System.out.println("S3 lifecycle policy configured successfully");
        } catch (Exception e) {
            System.err.println("Failed to configure S3 lifecycle policy: " + e.getMessage());
            e.printStackTrace();
        }
        */
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
