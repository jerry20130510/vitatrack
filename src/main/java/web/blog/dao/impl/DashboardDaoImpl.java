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
        String sql = """
            SELECT
              COALESCE(SUM(CASE WHEN ae.engagement_type = 'view' THEN ae.engagement_count ELSE 0 END), 0) as totalViews,
              COALESCE(SUM(CASE WHEN ae.engagement_type = 'like' THEN ae.engagement_count ELSE 0 END), 0) as totalLikes,
              COALESCE(SUM(CASE WHEN ae.engagement_type = 'share' THEN ae.engagement_count ELSE 0 END), 0) as totalShares,
              COALESCE(SUM(CASE WHEN ae.engagement_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)
                AND ae.engagement_type = 'view' THEN ae.engagement_count ELSE 0 END), 0) as currentViews,
              COALESCE(SUM(CASE WHEN ae.engagement_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)
                AND ae.engagement_type = 'like' THEN ae.engagement_count ELSE 0 END), 0) as currentLikes,
              COALESCE(SUM(CASE WHEN ae.engagement_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)
                AND ae.engagement_type = 'share' THEN ae.engagement_count ELSE 0 END), 0) as currentShares,
              COALESCE(SUM(CASE WHEN ae.engagement_date >= DATE_SUB(CURDATE(), INTERVAL 14 DAY)
                AND ae.engagement_date < DATE_SUB(CURDATE(), INTERVAL 7 DAY)
                AND ae.engagement_type = 'view' THEN ae.engagement_count ELSE 0 END), 0) as previousViews,
              COALESCE(SUM(CASE WHEN ae.engagement_date >= DATE_SUB(CURDATE(), INTERVAL 14 DAY)
                AND ae.engagement_date < DATE_SUB(CURDATE(), INTERVAL 7 DAY)
                AND ae.engagement_type = 'like' THEN ae.engagement_count ELSE 0 END), 0) as previousLikes,
              COALESCE(SUM(CASE WHEN ae.engagement_date >= DATE_SUB(CURDATE(), INTERVAL 14 DAY)
                AND ae.engagement_date < DATE_SUB(CURDATE(), INTERVAL 7 DAY)
                AND ae.engagement_type = 'share' THEN ae.engagement_count ELSE 0 END), 0) as previousShares
            FROM article_engagements ae
            JOIN articles a ON ae.article_id = a.id
            WHERE a.author_slug = :authorSlug
            """;

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
        String sql = """
            SELECT category, COUNT(*) as articleCount
            FROM articles
            WHERE author_slug = :authorSlug
            GROUP BY category
            ORDER BY articleCount DESC
            """;

        return getSession()
            .createNativeQuery(sql)
            .setParameter("authorSlug", authorSlug)
            .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
            .list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getDailyTrend(String authorSlug) {
        String sql = """
            SELECT
              DATE_FORMAT(ae.engagement_date, '%Y-%m-%d') as date,
              SUM(CASE WHEN ae.engagement_type = 'view' THEN ae.engagement_count ELSE 0 END) as views,
              SUM(CASE WHEN ae.engagement_type = 'like' THEN ae.engagement_count ELSE 0 END) as likes,
              SUM(CASE WHEN ae.engagement_type = 'share' THEN ae.engagement_count ELSE 0 END) as shares
            FROM article_engagements ae
            JOIN articles a ON ae.article_id = a.id
            WHERE a.author_slug = :authorSlug
              AND ae.engagement_date >= DATE_SUB(CURDATE(), INTERVAL 14 DAY)
              AND ae.engagement_date < CURDATE()
            GROUP BY ae.engagement_date
            ORDER BY ae.engagement_date ASC
            """;

        return getSession()
            .createNativeQuery(sql)
            .setParameter("authorSlug", authorSlug)
            .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
            .list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getMonthlyTrend(String authorSlug) {
        String sql = """
            SELECT
              DATE_FORMAT(ae.engagement_date, '%Y-%m') as month,
              SUM(ae.engagement_count) as views
            FROM article_engagements ae
            JOIN articles a ON ae.article_id = a.id
            WHERE a.author_slug = :authorSlug
              AND ae.engagement_type = 'view'
              AND ae.engagement_date >= DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 12 MONTH), '%Y-%m-01')
              AND ae.engagement_date < DATE_FORMAT(CURDATE(), '%Y-%m-01')
            GROUP BY DATE_FORMAT(ae.engagement_date, '%Y-%m')
            ORDER BY month ASC
            """;

        return getSession()
            .createNativeQuery(sql)
            .setParameter("authorSlug", authorSlug)
            .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
            .list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getTrafficSources(String authorSlug) {
        String sql = """
            SELECT
              ts.source_type as sourceType,
              SUM(ts.visit_count) as visitCount
            FROM traffic_sources ts
            JOIN articles a ON ts.article_id = a.id
            WHERE a.author_slug = :authorSlug
              AND ts.visit_date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)
              AND ts.visit_date < CURDATE()
            GROUP BY ts.source_type
            ORDER BY visitCount DESC
            """;

        return getSession()
            .createNativeQuery(sql)
            .setParameter("authorSlug", authorSlug)
            .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
            .list();
    }
}
