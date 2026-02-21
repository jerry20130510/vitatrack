package web.blog.service.impl;

import core.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import web.blog.dao.BloggerDao;
import web.blog.dao.impl.BloggerDaoImpl;
import web.blog.service.BloggerService;
import web.blog.service.SlugGeneratorService;
import web.blog.vo.Blogger;

import javax.naming.NamingException;
import java.sql.Timestamp;

public class BloggerServiceImpl implements BloggerService {
    private final BloggerDao bloggerDao = new BloggerDaoImpl();
    private final SlugGeneratorService slugGeneratorService;

    public BloggerServiceImpl() throws NamingException {
        this.slugGeneratorService = new SlugGeneratorServiceImpl();
    }

    @Override
    public Blogger findByEmail(String email) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Blogger result = bloggerDao.findByEmail(email);
            tx.commit();
            return result;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException(e);
        }
    }

    @Override
    public Blogger findByGoogleSub(String googleSub) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Blogger result = bloggerDao.findByGoogleSub(googleSub);
            tx.commit();
            return result;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException(e);
        }
    }

    @Override
    public Blogger createFromOAuth(String googleSub, String email, String name, String picture) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Blogger blogger = new Blogger();
            blogger.setGoogleSub(googleSub);
            blogger.setGoogleEmail(email);
            blogger.setEmail(email);
            blogger.setDisplayName(name);
            blogger.setProfileImage(picture);
            blogger.setRole("BLOGGER");
            blogger.setProfileComplete(false);
            blogger.setAuthorSlug(slugGeneratorService.generateSlug(name));
            blogger.setLastLogin(new Timestamp(System.currentTimeMillis()));
            bloggerDao.insert(blogger);
            Blogger result = bloggerDao.findByGoogleSub(googleSub);
            tx.commit();
            return result;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateFromOAuth(Blogger blogger, String name, String picture) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            blogger.setDisplayName(name);
            blogger.setProfileImage(picture);
            blogger.setLastLogin(new Timestamp(System.currentTimeMillis()));
            bloggerDao.update(blogger);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException(e);
        }
    }
}
