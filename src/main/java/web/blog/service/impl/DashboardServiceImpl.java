package web.blog.service.impl;

import core.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import web.blog.dao.DashboardDao;
import web.blog.dao.impl.DashboardDaoImpl;
import web.blog.service.DashboardService;

import java.util.List;
import java.util.Map;

public class DashboardServiceImpl implements DashboardService {
    private static final String STATS_CURRENT_VIEWS = "currentViews";
    private static final String STATS_CURRENT_LIKES = "currentLikes";
    private static final String STATS_CURRENT_SHARES = "currentShares";
    private static final String STATS_PREVIOUS_VIEWS = "previousViews";
    private static final String STATS_PREVIOUS_LIKES = "previousLikes";
    private static final String STATS_PREVIOUS_SHARES = "previousShares";
    
    private static final String RESPONSE_TOTAL_VIEWS = "totalViews";
    private static final String RESPONSE_TOTAL_LIKES = "totalLikes";
    private static final String RESPONSE_TOTAL_SHARES = "totalShares";
    private static final String RESPONSE_VIEWS_GROWTH = "viewsGrowth";
    private static final String RESPONSE_LIKES_GROWTH = "likesGrowth";
    private static final String RESPONSE_SHARES_GROWTH = "sharesGrowth";
    private static final String RESPONSE_CATEGORY_STATS = "categoryStats";
    private static final String RESPONSE_DAILY_TREND = "dailyTrend";
    private static final String RESPONSE_MONTHLY_TREND = "monthlyTrend";
    private static final String RESPONSE_TRAFFIC_SOURCES = "trafficSources";
    
    private static final double GROWTH_PERCENTAGE_MULTIPLIER = 100.0;
    private static final double GROWTH_NO_PREVIOUS_DATA = 100.0;
    private static final double GROWTH_NO_CURRENT_DATA = -100.0;
    
    private final DashboardDao dashboardDao = new DashboardDaoImpl();

    @Override
    public Map<String, Object> getDashboardData(String authorSlug) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Map<String, Object> stats = dashboardDao.getStats(authorSlug);
            if (stats == null) {
                tx.commit();
                return null;
            }

            List<Map<String, Object>> categoryStats = dashboardDao.getCategoryStats(authorSlug);
            List<Map<String, Object>> dailyTrend = dashboardDao.getDailyTrend(authorSlug);
            List<Map<String, Object>> monthlyTrend = dashboardDao.getMonthlyTrend(authorSlug);
            List<Map<String, Object>> trafficSources = dashboardDao.getTrafficSources(authorSlug);

            Long currentViews = getLongValue(stats, STATS_CURRENT_VIEWS);
            Long currentLikes = getLongValue(stats, STATS_CURRENT_LIKES);
            Long currentShares = getLongValue(stats, STATS_CURRENT_SHARES);
            Long previousViews = getLongValue(stats, STATS_PREVIOUS_VIEWS);
            Long previousLikes = getLongValue(stats, STATS_PREVIOUS_LIKES);
            Long previousShares = getLongValue(stats, STATS_PREVIOUS_SHARES);

            Map<String, Object> response = Map.of(
                RESPONSE_TOTAL_VIEWS, currentViews,
                RESPONSE_TOTAL_LIKES, currentLikes,
                RESPONSE_TOTAL_SHARES, currentShares,
                RESPONSE_VIEWS_GROWTH, calculateGrowth(currentViews, previousViews),
                RESPONSE_LIKES_GROWTH, calculateGrowth(currentLikes, previousLikes),
                RESPONSE_SHARES_GROWTH, calculateGrowth(currentShares, previousShares),
                RESPONSE_CATEGORY_STATS, categoryStats,
                RESPONSE_DAILY_TREND, dailyTrend,
                RESPONSE_MONTHLY_TREND, monthlyTrend,
                RESPONSE_TRAFFIC_SOURCES, trafficSources
            );

            tx.commit();
            return response;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw new RuntimeException(e);
        }
    }

    private Long getLongValue(Map<String, Object> map, String key) {
        return ((Number) map.get(key)).longValue();
    }

    private Double calculateGrowth(Long current, Long previous) {
        if (previous == null || previous == 0) {
            return current != null && current > 0 ? GROWTH_NO_PREVIOUS_DATA : 0.0;
        }
        if (current == null) {
            return GROWTH_NO_CURRENT_DATA;
        }
        return ((current - previous) * GROWTH_PERCENTAGE_MULTIPLIER) / previous;
    }
}
