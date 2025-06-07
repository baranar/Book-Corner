package com.MyBookCorner.controller;

import com.MyBookCorner.dao.BookDAO;
import com.MyBookCorner.dao.ReviewDAO;
import com.MyBookCorner.model.Book;
import com.MyBookCorner.model.Review;
import com.MyBookCorner.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet({"/book-detail", "/add-review", "/edit-review", "/delete-review"})
public class ReviewServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private BookDAO bookDAO;
    private ReviewDAO reviewDAO;

    public ReviewServlet() {
        super();
        this.bookDAO = new BookDAO();
        this.reviewDAO = new ReviewDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();

        switch (path) {
            case "/book-detail":
                showBookDetail(request, response);
                break;
            case "/edit-review":
                showEditReviewForm(request, response);
                break;
            case "/delete-review":
                deleteReview(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/books");
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();

        switch (path) {
            case "/add-review": // This handles both adding and updating a review
                addOrUpdateReview(request, response);
                break;
            case "/edit-review": // POST request for updating an existing review
                addOrUpdateReview(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/books");
                break;
        }
    }

    private void showBookDetail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int bookId;
        try {
            bookId = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            request.setAttribute("message", "Invalid book ID.");
            request.setAttribute("messageType", "danger");
            response.sendRedirect(request.getContextPath() + "/books");
            return;
        }

        Book book = bookDAO.getBookById(bookId);
        if (book == null) {
            // Book not found, will be handled by JSP's 'book == null' check
            request.getRequestDispatcher("/WEB-INF/views/book-detail.jsp").forward(request, response);
            return;
        }

        List<Review> reviews = reviewDAO.getReviewsByBookId(bookId);
        request.setAttribute("book", book);
        request.setAttribute("reviews", reviews);

        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("currentUser") != null) {
            User currentUser = (User) session.getAttribute("currentUser");
            // Check if current user already reviewed this book to pre-fill the form
            Review userReview = reviewDAO.getReviewByBookIdAndUserId(bookId, currentUser.getId());
            request.setAttribute("userReview", userReview);
        }

        request.getRequestDispatcher("/WEB-INF/views/book-detail.jsp").forward(request, response);
    }

    private void addOrUpdateReview(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            request.setAttribute("message", "Please log in to leave a review.");
            request.setAttribute("messageType", "danger");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User currentUser = (User) session.getAttribute("currentUser");
        int bookId;
        int rating;
        String comment = request.getParameter("comment");

        try {
            bookId = Integer.parseInt(request.getParameter("bookId"));
            rating = Integer.parseInt(request.getParameter("rating"));
        } catch (NumberFormatException e) {
            request.setAttribute("message", "Invalid book ID or rating.");
            request.setAttribute("messageType", "danger");
            response.sendRedirect(request.getContextPath() + "/book-detail?id=" + request.getParameter("bookId")); // Redirect back to book detail
            return;
        }

        if (rating < 1 || rating > 5) {
            request.setAttribute("message", "Rating must be between 1 and 5.");
            request.setAttribute("messageType", "danger");
            response.sendRedirect(request.getContextPath() + "/book-detail?id=" + bookId);
            return;
        }

        Review review = new Review();
        review.setBookId(bookId);
        review.setUserId(currentUser.getId());
        review.setRating(rating);
        review.setComment(comment != null ? comment : "");

        if (reviewDAO.addReview(review)) {
            request.setAttribute("message", "Review successfully submitted!");
            request.setAttribute("messageType", "success");
        } else {
            request.setAttribute("message", "Error submitting review. Please try again.");
            request.setAttribute("messageType", "danger");
        }

        response.sendRedirect(request.getContextPath() + "/book-detail?id=" + bookId);
    }

    private void showEditReviewForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            request.setAttribute("message", "Please log in to edit a review.");
            request.setAttribute("messageType", "danger");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User currentUser = (User) session.getAttribute("currentUser");
        int reviewId;
        int bookId;
        try {
            reviewId = Integer.parseInt(request.getParameter("id"));
            bookId = Integer.parseInt(request.getParameter("bookId")); // Get bookId to redirect back
        } catch (NumberFormatException e) {
            request.setAttribute("message", "Invalid review or book ID.");
            request.setAttribute("messageType", "danger");
            response.sendRedirect(request.getContextPath() + "/books");
            return;
        }

        Review reviewToEdit = reviewDAO.getReviewByBookIdAndUserId(bookId, currentUser.getId()); // Get the review by bookId and userId to ensure it's THEIR review

        if (reviewToEdit == null || reviewToEdit.getId() != reviewId) { // Also check if the ID matches what was requested
            request.setAttribute("message", "Review not found or you don't have permission to edit it.");
            request.setAttribute("messageType", "danger");
            response.sendRedirect(request.getContextPath() + "/book-detail?id=" + bookId);
            return;
        }

        request.setAttribute("userReview", reviewToEdit);
        request.setAttribute("book", bookDAO.getBookById(bookId));
        request.setAttribute("reviews", reviewDAO.getReviewsByBookId(bookId));
        request.getRequestDispatcher("/WEB-INF/views/book-detail.jsp").forward(request, response);
    }


    private void deleteReview(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            request.setAttribute("message", "Please log in to delete a review.");
            request.setAttribute("messageType", "danger");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User currentUser = (User) session.getAttribute("currentUser");
        int reviewId;
        int bookId;
        try {
            reviewId = Integer.parseInt(request.getParameter("id"));
            bookId = Integer.parseInt(request.getParameter("bookId")); // Get bookId to redirect back
        } catch (NumberFormatException e) {
            request.setAttribute("message", "Invalid review or book ID.");
            request.setAttribute("messageType", "danger");
            response.sendRedirect(request.getContextPath() + "/books");
            return;
        }

        if (reviewDAO.deleteReview(reviewId, currentUser.getId())) {
            request.setAttribute("message", "Review successfully deleted!");
            request.setAttribute("messageType", "success");
        } else {
            request.setAttribute("message", "Error deleting review or you don't have permission.");
            request.setAttribute("messageType", "danger");
        }

        response.sendRedirect(request.getContextPath() + "/book-detail?id=" + bookId);
    }
}
