package com.MyBookCorner.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/kitapkosem_db";
    private static final String USER = "postgres"; // PostgreSQL kullanıcı adınız
    private static final String PASSWORD = "Baran1905"; // PostgreSQL şifreniz

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC Driver bulunamadı!");
            e.printStackTrace();
            throw new SQLException("Veritabanı sürücüsü yüklenemedi.", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Veritabanı bağlantısı kapatılırken hata oluştu: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
