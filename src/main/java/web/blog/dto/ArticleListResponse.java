package web.blog.dto;

import web.blog.vo.Article;

import java.util.List;

public class ArticleListResponse {
    private int page;
    private int size;
    private int totalElements;
    private int totalPages;
    private List<Article> articles;

    public ArticleListResponse() {}

    public ArticleListResponse(int page, int size, int totalElements, int totalPages, List<Article> articles) {
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.articles = articles;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(int totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }
}
