package core;

import javax.sql.DataSource;

public class DataSourceProvider {

    private static DataSource dataSource;

    private DataSourceProvider() {
    }

    public static void setDataSource(DataSource ds) {
        dataSource = ds;
    }

    public static DataSource getDataSource() {
        if (dataSource == null) {
            throw new IllegalStateException("DataSource 尚未初始化");
        }
        return dataSource;
    }
}
