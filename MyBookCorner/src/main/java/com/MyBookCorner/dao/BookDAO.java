package com.MyBookCorner.dao;

import com.MyBookCorner.model.Book;
import com.MyBookCorner.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    public boolean addBook(Book book) {
        String SQL = "INSERT INTO books (title, author, description, added_by_user_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getDescription());
            pstmt.setInt(4, book.getAddedByUserId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error adding book: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String SQL = "SELECT b.id, b.title, b.author, b.description, b.added_by_user_id, b.created_at, " +
                "COALESCE(AVG(r.rating), 0.0) AS average_rating " +
                "FROM books b LEFT JOIN reviews r ON b.id = r.book_id " +
                "GROUP BY b.id, b.title, b.author, b.description, b.added_by_user_id, b.created_at " +
                "ORDER BY b.created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Book book = new Book();
                book.setId(rs.getInt("id"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setDescription(rs.getString("description"));
                book.setAddedByUserId(rs.getInt("added_by_user_id"));
                book.setCreatedAt(rs.getTimestamp("created_at"));
                book.setAverageRating(rs.getDouble("average_rating"));
                books.add(book);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all books: " + e.getMessage());
            e.printStackTrace();
        }
        return books;
    }

    public List<Book> searchBooks(String searchTerm) {
        List<Book> books = new ArrayList<>();
        String SQL = "SELECT b.id, b.title, b.author, b.description, b.added_by_user_id, b.created_at, " +
                "COALESCE(AVG(r.rating), 0.0) AS average_rating " +
                "FROM books b LEFT JOIN reviews r ON b.id = r.book_id " +
                "WHERE LOWER(b.title) LIKE LOWER(?) OR LOWER(b.author) LIKE LOWER(?) " +
                "GROUP BY b.id, b.title, b.author, b.description, b.added_by_user_id, b.created_at " +
                "ORDER BY b.created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, "%" + searchTerm + "%");
            pstmt.setString(2, "%" + searchTerm + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Book book = new Book();
                book.setId(rs.getInt("id"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setDescription(rs.getString("description"));
                book.setAddedByUserId(rs.getInt("added_by_user_id"));
                book.setCreatedAt(rs.getTimestamp("created_at"));
                book.setAverageRating(rs.getDouble("average_rating"));
                books.add(book);
            }
        } catch (SQLException e) {
            System.err.println("Error during book search: " + e.getMessage());
            e.printStackTrace();
        }
        return books;
    }

    public Book getBookById(int bookId) {
        String SQL = "SELECT b.id, b.title, b.author, b.description, b.added_by_user_id, b.created_at, " +
                "COALESCE(AVG(r.rating), 0.0) AS average_rating " +
                "FROM books b LEFT JOIN reviews r ON b.id = r.book_id " +
                "WHERE b.id = ? " +
                "GROUP BY b.id, b.title, b.author, b.description, b.added_by_user_id, b.created_at";
        Book book = null;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setInt(1, bookId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                book = new Book();
                book.setId(rs.getInt("id"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setDescription(rs.getString("description"));
                book.setAddedByUserId(rs.getInt("added_by_user_id"));
                book.setCreatedAt(rs.getTimestamp("created_at"));
                book.setAverageRating(rs.getDouble("average_rating"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching book details: " + e.getMessage());
            e.printStackTrace();
        }
        return book;
    }

    public boolean updateBook(Book book) {
        String SQL = "UPDATE books SET title = ?, author = ?, description = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getDescription());
            pstmt.setInt(4, book.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating book: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteBook(int bookId) {
        String SQL = "DELETE FROM books WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setInt(1, bookId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting book: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteBooksByUserId(int userId) {
        String SQL = "DELETE FROM books WHERE added_by_user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL)) {

            pstmt.setInt(1, userId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting user's books: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}