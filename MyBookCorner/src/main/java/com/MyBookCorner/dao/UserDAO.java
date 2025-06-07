package com.MyBookCorner.dao;

import com.MyBookCorner.model.User;
import com.MyBookCorner.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    public boolean registerUser(User user) {
        String SQL = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getEmail());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Kullanıcı kaydı sırasında hata oluştu: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public User loginUser(String username, String password) {
        String SQL = "SELECT id, username, password, email, created_at FROM users WHERE username = ?";
        User user = null;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                 if (rs.getString("password").equals(password)) {
                    user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setEmail(rs.getString("email"));
                    user.setCreatedAt(rs.getTimestamp("created_at"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Kullanıcı girişi sırasında hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
        return user;
    }

    public User getUserByUsername(String username) {
        String SQL = "SELECT id, username, password, email, created_at FROM users WHERE username = ?";
        User user = null;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                user.setCreatedAt(rs.getTimestamp("created_at"));
            }
        } catch (SQLException e) {
            System.err.println("Kullanıcı adı ile arama sırasında hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
        return user;
    }

    public User getUserById(int userId) {
        String SQL = "SELECT id, username, password, email, created_at FROM users WHERE id = ?";
        User user = null;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                user.setCreatedAt(rs.getTimestamp("created_at"));
            }
        } catch (SQLException e) {
            System.err.println("Kullanıcı ID ile arama sırasında hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
        return user;
    }

    public boolean updateUser(User user) {
        String SQL;
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            SQL = "UPDATE users SET username = ?, password = ?, email = ? WHERE id = ?";
        } else {
            SQL = "UPDATE users SET username = ?, email = ? WHERE id = ?";
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, user.getUsername());
            int paramIndex = 2;
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                pstmt.setString(paramIndex++, user.getPassword()); // Güvenlik Notu: Hashlenmiş şifre olmalı!
            }
            pstmt.setString(paramIndex++, user.getEmail());
            pstmt.setInt(paramIndex, user.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Kullanıcı güncelleme sırasında hata oluştu: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUser(int userId) {
        String SQL = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setInt(1, userId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Kullanıcı silme sırasında hata oluştu: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}