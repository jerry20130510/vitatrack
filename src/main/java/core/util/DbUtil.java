package core.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DbUtil {

    private static final String url;
    private static final String username;
    private static final String password;

    static {
        try (InputStream is = DbUtil.class
                .getClassLoader()
                .getResourceAsStream("db.properties")) {

            Properties props = new Properties();
            props.load(is);

            url = props.getProperty("db.url");
            username = props.getProperty("db.username");
            password = props.getProperty("db.password");

        } catch (Exception e) {
            throw new RuntimeException("Failed to load db.properties", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    // 🔧 先測試用
    public static void main(String[] args) {
        try (Connection conn = DbUtil.getConnection()) {
            System.out.println("✅ DB Connected: " + conn.getMetaData().getURL());
        } catch (SQLException e) {
            System.out.println("❌ DB Connect Failed");
            e.printStackTrace();
        }
    }
}
