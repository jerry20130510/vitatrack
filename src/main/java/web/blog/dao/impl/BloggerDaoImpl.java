package web.blog.dao.impl;

import core.util.HibernateUtil;
import org.hibernate.Session;
import web.blog.dao.BloggerDao;
import web.blog.vo.Blogger;

public class BloggerDaoImpl implements BloggerDao {

    private Session getSession() {
        return HibernateUtil.getSessionFactory().getCurrentSession();
    }

    @Override
    public Blogger findByEmail(String email) {
        return getSession()
            .createQuery("FROM Blogger WHERE email = :email", Blogger.class)
            .setParameter("email", email)
            .uniqueResult();
    }

    @Override
    public Blogger findByAuthorSlug(String authorSlug) {
        return getSession()
            .createQuery("FROM Blogger WHERE authorSlug = :authorSlug", Blogger.class)
            .setParameter("authorSlug", authorSlug)
            .uniqueResult();
    }

    @Override
    public Blogger findByGoogleSub(String googleSub) {
        return getSession()
            .createQuery("FROM Blogger WHERE googleSub = :googleSub", Blogger.class)
            .setParameter("googleSub", googleSub)
            .uniqueResult();
    }

    @Override
    public int insert(Blogger blogger) {
        getSession().persist(blogger);
        return 1;
    }

    @Override
    public int update(Blogger blogger) {
        getSession().merge(blogger);
        return 1;
    }
}
