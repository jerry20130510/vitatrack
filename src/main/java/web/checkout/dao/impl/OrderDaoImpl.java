package web.checkout.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import web.checkout.dao.OrderDao;

public class OrderDaoImpl implements OrderDao {

    @Override
    public int insertOrder(Connection conn,
                           int memberId,
                           int totalAmount,
                           String status,
                           String paymentMethod,
                           String paymentStatus,
                           int amount,
                           String transactionId) throws SQLException {

        String sql =
            "INSERT INTO orders " +
            "(member_id, status, total_amount, payment_method, payment_status, amount, transaction_id) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, memberId);
            ps.setString(2, status);          // enum: Paid/Unpaid/Shipped
            ps.setInt(3, totalAmount);        // int
            ps.setString(4, paymentMethod);   // varchar(50)
            ps.setString(5, paymentStatus);   // varchar(20)
            ps.setInt(6, amount);             // int
            ps.setString(7, transactionId);   // varchar(50)

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        throw new SQLException("Insert order failed, no generated key returned.");
    }
}
