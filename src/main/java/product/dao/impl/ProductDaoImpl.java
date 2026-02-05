package product.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import product.dao.ProductDao;

public class ProductDaoImpl implements ProductDao {

    private DataSource ds;

    public ProductDaoImpl() {
        try {
            ds = (DataSource) new InitialContext()
                    .lookup("java:comp/env/jdbc/vitatrack");
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getProductStock(int productId) {
        String sql = "SELECT stock FROM products_test WHERE product_id = ?";
        
        System.out.println("【DAO SQL】" + sql);

        try (
                Connection conn = ds.getConnection();   // ← ① 這一行「已經存在」
            ) {

                // ⭐⭐ 就是「加在這裡」⭐⭐
                System.out.println("【DB URL】" + conn.getMetaData().getURL());
                System.out.println("【DB NAME】" + conn.getCatalog());

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, productId);
                    ResultSet rs = ps.executeQuery();

                    if (rs.next()) {
                        return rs.getInt("stock");
                    }
                    return 0;
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
    }
}
