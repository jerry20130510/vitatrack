package web.blog.dao.impl;

import core.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import web.blog.dao.DashboardDao;

import java.util.List;
import java.util.Map;

public class DashboardDaoImpl implements DashboardDao {

    private Session getSession() {
        return HibernateUtil.getSessionFactory().getCurrentSession();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> getStats(String authorSlug) {
        String sql = "SELECT\n" +
            "  COALESCE(SUM(CASE WHEN ae.engagement_type = 'view' THEN ae.engagement_count ELSE 0 END), 0) as totalViews,\n" +
            "  COALESCE(SUM(CASE WHEN ae.engagement_type = 'like' THEN ae.engagement_count ELSE 0 END), 0) as totalLikes,\n" +
            "  COALESCE(SUM(CASE WHEN ae.engagement_type = 'share' THEN ae.engagement_count ELSE 0 END), 0) as totalShares,\n" +
            "  COALESCE(SUM(CASE WHEN ae.engagement_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)\n" +
            "    AND ae.engagement_type = 'view' THEN ae.engagement_count ELSE 0 END), 0) as currentViews,\n" +
            "  COALESCE(SUM(CASE WHEN ae.engagement_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)\n" +
            "    AND ae.engagement_type = 'like' THEN ae.engagement_count ELSE 0 END), 0) as currentLikes,\n" +
            "  COALESCE(SUM(CASE WHEN ae.engagement_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)\n" +
            "    AND ae.engagement_type = 'share' THEN ae.engagement_count ELSE 0 END), 0) as currentShares,\n" +
            "  COALESCE(SUM(CASE WHEN ae.engagement_date >= DATE_SUB(CURDATE(), INTERVAL 14 DAY)\n" +
            "    AND ae.engagement_date < DATE_SUB(CURDATE(), INTERVAL 7 DAY)\n" +
            "    AND ae.engagement_type = 'view' THEN ae.engagement_count ELSE 0 END), 0) as previousViews,\n" +
            "  COALESCE(SUM(CASE WHEN ae.engagement_date >= DATE_SUB(CURDATE(), INTERVAL 14 DAY)\n" +
            "    AND ae.engagement_date < DATE_SUB(CURDATE(), INTERVAL 7 DAY)\n" +
            "    AND ae.engagement_type = 'like' THEN ae.engagement_count ELSE 0 END), 0) as previousLikes,\n" +
            "  COALESCE(SUM(CASE WHEN ae.engagement_date >= DATE_SUB(CURDATE(), INTERVAL 14 DAY)\n" +
            "    AND ae.engagement_date < DATE_SUB(CURDATE(), INTERVAL 7 DAY)\n" +
            "    AND ae.engagement_type = 'share' THEN ae.engagement_count ELSE 0 END), 0) as previousShares\n" +
            "FROM article_engagements ae\n" +
            "JOIN articles a ON ae.article_id = a.id\n" +
            "WHERE a.author_slug = :authorSlug";

        List<Map<String, Object>> results = getSession()
            .createNativeQuery(sql)
            .setParameter("authorSlug", authorSlug)
            .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
            .list();

        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getCategoryStats(String authorSlug) {
        String sql = "SELECT category, COUNT(*) as articleCount\n" +
            "FROM articles\n" +
            "WHERE author_slug = :authorSlug\n" +
            "GROUP BY category\n" +
            "ORDER BY articleCount DESC";

        return getSession()
            .createNativeQuery(sql)
            .setParameter("authorSlug", authorSlug)
            .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
            .list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getDailyTrend(String authorSlug) {
        String sql = "SELECT\n" +
            "  DATE_FORMAT(ae.engagement_date, '%Y-%m-%d') as date,\n" +
            "  SUM(CASE WHEN ae.engagement_type = 'view' THEN ae.engagement_count ELSE 0 END) as views,\n" +
            "  SUM(CASE WHEN ae.engagement_type = 'like' THEN ae.engagement_count ELSE 0 END) as likes,\n" +
            "  SUM(CASE WHEN ae.engagement_type = 'share' THEN ae.engagement_count ELSE 0 END) as shares\n" +
            "FROM article_engagements ae\n" +
            "JOIN articles a ON ae.article_id = a.id\n" +
            "WHERE a.author_slug = :authorSlug\n" +
            "  AND ae.engagement_date >= DATE_SUB(CURDATE(), INTERVAL 14 DAY)\n" +
            "  AND ae.engagement_date < CURDATE()\n" +
            "GROUP BY ae.engagement_date\n" +
            "ORDER BY ae.engagement_date ASC";

        return getSession()
            .createNativeQuery(sql)
            .setParameter("authorSlug", authorSlug)
            .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
            .list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getMonthlyTrend(String authorSlug) {
        String sql = "SELECT\n" +
            "  DATE_FORMAT(ae.engagement_date, '%Y-%m') as month,\n" +
            "  SUM(ae.engagement_count) as views\n" +
            "FROM article_engagements ae\n" +
            "JOIN articles a ON ae.article_id = a.id\n" +
            "WHERE a.author_slug = :authorSlug\n" +
            "  AND ae.engagement_type = 'view'\n" +
            "  AND ae.engagement_date >= DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 12 MONTH), '%Y-%m-01')\n" +
            "  AND ae.engagement_date < DATE_FORMAT(CURDATE(), '%Y-%m-01')\n" +
            "GROUP BY DATE_FORMAT(ae.engagement_date, '%Y-%m')\n" +
            "ORDER BY month ASC";

        return getSession()
            .createNativeQuery(sql)
            .setParameter("authorSlug", authorSlug)
            .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
            .list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getTrafficSources(String authorSlug) {
        String sql = "SELECT\n" +
            "  ts.source_type as sourceType,\n" +
            "  SUM(ts.visit_count) as visitCount\n" +
            "FROM traffic_sources ts\n" +
            "JOIN articles a ON ts.article_id = a.id\n" +
            "WHERE a.author_slug = :authorSlug\n" +
            "  AND ts.visit_date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)\n" +
            "  AND ts.visit_date < CURDATE()\n" +
            "GROUP BY ts.source_type\n" +
            "ORDER BY visitCount DESC";

        return getSession()
            .createNativeQuery(sql)
            .setParameter("authorSlug", authorSlug)
            .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
            .list();
    }
}
