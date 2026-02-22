package web.blog.service;

import web.blog.vo.Blogger;

public interface BloggerService {
    Blogger findByEmail(String email);
    Blogger findByGoogleSub(String googleSub);
    Blogger createFromOAuth(String googleSub, String email, String name, String picture);
    void updateFromOAuth(Blogger blogger, String name, String picture);
}
