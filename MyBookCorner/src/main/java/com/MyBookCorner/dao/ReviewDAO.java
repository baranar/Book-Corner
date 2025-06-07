package com.MyBookCorner.dao;

import com.MyBookCorner.model.Review;
import com.MyBookCorner.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {

    public boolean addReview(Review review) {
        String SQL = "INSERT INTO reviews (book_id, user_id, rating, comment) VALUES (?, ?, ?, ?) " +
                "ON CONFLICT (book_id, user_id) DO UPDATE SET rating = EXCLUDED.rating, comment = EXCLUDED.comment, created_at = CURRENT_TIMESTAMP";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setInt(1, review.getBookId());
            pstmt.setInt(2, review.getUserId());
            pstmt.setInt(3, review.getRating());
            pstmt.setString(4, review.getComment());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error adding/updating review: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Review> getReviewsByBookId(int bookId) {
        List<Review> reviews = new ArrayList<>();
        String SQL = "SELECT r.id, r.book_id, r.user_id, r.rating, r.comment, r.created_at, u.username " +
                "FROM reviews r JOIN users u ON r.user_id = u.id " +
                "WHERE r.book_id = ? ORDER BY r.created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setInt(1, bookId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Review review = new Review();
                review.setId(rs.getInt("id"));
                review.setBookId(rs.getInt("book_id"));
                review.setUserId(rs.getInt("user_id"));
                review.setRating(rs.getInt("rating"));
                review.setComment(rs.getString("comment"));
                review.setCreatedAt(rs.getTimestamp("created_at"));
                review.setUsername(rs.getString("username")); // Set the username
                reviews.add(review);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching book reviews: " + e.getMessage());
            e.printStackTrace();
        }
        return reviews;
    }

    public Review getReviewByBookIdAndUserId(int bookId, int userId) {
        String SQL = "SELECT id, book_id, user_id, rating, comment, created_at FROM reviews WHERE book_id = ? AND user_id = ?";
        Review review = null;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setInt(1, bookId);
            pstmt.setInt(2, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                review = new Review();
                review.setId(rs.getInt("id"));
                review.setBookId(rs.getInt("book_id"));
                review.setUserId(rs.getInt("user_id"));
                review.setRating(rs.getInt("rating"));
                review.setComment(rs.getString("comment"));
                review.setCreatedAt(rs.getTimestamp("created_at"));
            }
        } catch (SQLException e) {
            System.err.println("Error accessing specific review: " + e.getMessage());
            e.printStackTrace();
        }
        return review;
    }

    public boolean updateReview(Review review) {
        String SQL = "UPDATE reviews SET rating = ?, comment = ?, created_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setInt(1, review.getRating());
            pstmt.setString(2, review.getComment());
            pstmt.setInt(3, review.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating review: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteReview(int reviewId, int userId) {
        String SQL = "DELETE FROM reviews WHERE id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setInt(1, reviewId);
            pstmt.setInt(2, userId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting review: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteReviewsByUserId(int userId) {
        String SQL = "DELETE FROM reviews WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setInt(1, userId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting user's reviews: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}