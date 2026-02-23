package web.blog.dao;

import java.util.List;
import java.util.Map;

public interface DashboardDao {
    Map<String, Object> getStats(String authorSlug);
    List<Map<String, Object>> getCategoryStats(String authorSlug);
    List<Map<String, Object>> getDailyTrend(String authorSlug);
    List<Map<String, Object>> getMonthlyTrend(String authorSlug);
    List<Map<String, Object>> getTrafficSources(String authorSlug);
}
