package web.blog.dto;

import java.util.List;

public class ArticleListResponse<T> {
    private int page;
    private int size;
    private int totalElements;
    private int totalPages;
    private List<T> articles;

    public ArticleListResponse() {}

    public ArticleListResponse(int page, int size, int totalElements, int totalPages, List<T> articles) {
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

    public List<T> getArticles() {
        return articles;
    }

    public void setArticles(List<T> articles) {
        this.articles = articles;
    }
}
