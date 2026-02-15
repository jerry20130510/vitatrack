package web.blog.dao;

import web.blog.vo.Blogger;

public interface BloggerDao {
    Blogger findByEmail(String email);
    Blogger findByAuthorSlug(String authorSlug);
    Blogger findByGoogleSub(String googleSub);
    int insert(Blogger blogger);
    int update(Blogger blogger);
}
