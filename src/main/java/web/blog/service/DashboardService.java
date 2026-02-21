package web.blog.service;

import java.util.Map;

public interface DashboardService {
    Map<String, Object> getDashboardData(String authorSlug);
}
