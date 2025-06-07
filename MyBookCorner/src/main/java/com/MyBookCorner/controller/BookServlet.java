package com.MyBookCorner.controller;

import com.MyBookCorner.dao.BookDAO;
import com.MyBookCorner.model.Book;
import com.MyBookCorner.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet({"/books", "/add-book", "/edit-book", "/delete-book"})
public class BookServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private BookDAO bookDAO;

    public BookServlet() {
        super();
        this.bookDAO = new BookDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();

        switch (path) {
            case "/books":
                listBooks(request, response);
                break;
            case "/add-book":
                showAddBookForm(request, response);
                break;
            case "/edit-book":
                showEditBookForm(request, response);
                break;
            case "/delete-book":
                deleteBook(request, response);
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
            case "/add-book":
                addBook(request, response);
                break;
            case "/edit-book":
                updateBook(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/books");
                break;
        }
    }

    private void listBooks(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String searchTerm = request.getParameter("search");
        List<Book> books;
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            books = bookDAO.searchBooks(searchTerm);
        } else {
            books = bookDAO.getAllBooks();
        }
        request.setAttribute("books", books);
        request.getRequestDispatcher("/WEB-INF/views/books.jsp").forward(request, response);
    }

    private void showAddBookForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            request.setAttribute("message", "Please log in to add a book.");
            request.setAttribute("messageType", "danger");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        // If logged in, show the form
        request.getRequestDispatcher("/WEB-INF/views/add-book.jsp").forward(request, response);
    }

    private void addBook(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            request.setAttribute("message", "Please log in to add a book.");
            request.setAttribute("messageType", "danger");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User currentUser = (User) session.getAttribute("currentUser");
        String title = request.getParameter("title");
        String author = request.getParameter("author");
        String description = request.getParameter("description");

        if (title == null || title.isEmpty() || author == null || author.isEmpty()) {
            request.setAttribute("message", "Title and Author are required.");
            request.setAttribute("messageType", "danger");
            request.setAttribute("book", new Book(0, title, author, description, 0, null, 0.0)); // To pre-fill form
            request.getRequestDispatcher("/WEB-INF/views/add-book.jsp").forward(request, response);
            return;
        }

        Book newBook = new Book();
        newBook.setTitle(title);
        newBook.setAuthor(author);
        newBook.setDescription(description);
        newBook.setAddedByUserId(currentUser.getId());

        if (bookDAO.addBook(newBook)) {
            request.setAttribute("message", "Book added successfully!");
            request.setAttribute("messageType", "success");
            response.sendRedirect(request.getContextPath() + "/books"); // Redirect to book list
        } else {
            request.setAttribute("message", "Error adding book. Please try again.");
            request.setAttribute("messageType", "danger");
            request.setAttribute("book", newBook); // To pre-fill form
            request.getRequestDispatcher("/WEB-INF/views/add-book.jsp").forward(request, response);
        }
    }

    private void showEditBookForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            request.setAttribute("message", "Please log in to edit a book.");
            request.setAttribute("messageType", "danger");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int bookId;
        try {
            bookId = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            request.setAttribute("message", "Invalid book ID.");
            request.setAttribute("messageType", "danger");
            response.sendRedirect(request.getContextPath() + "/books");
            return;
        }

        Book bookToEdit = bookDAO.getBookById(bookId);
        User currentUser = (User) session.getAttribute("currentUser");

        if (bookToEdit == null || currentUser.getId() != bookToEdit.getAddedByUserId()) {
            request.setAttribute("message", "Book not found or you don't have permission to edit this book.");
            request.setAttribute("messageType", "danger");
            response.sendRedirect(request.getContextPath() + "/books");
            return;
        }

        request.setAttribute("bookToEdit", bookToEdit); // Pass the book object to JSP
        request.getRequestDispatcher("/WEB-INF/views/edit-book.jsp").forward(request, response);
    }

    private void updateBook(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            request.setAttribute("message", "Please log in to update a book.");
            request.setAttribute("messageType", "danger");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User currentUser = (User) session.getAttribute("currentUser");
        int bookId;
        String title = request.getParameter("title");
        String author = request.getParameter("author");
        String description = request.getParameter("description");

        try {
            bookId = Integer.parseInt(request.getParameter("id")); // Get the book ID from the hidden input
        } catch (NumberFormatException e) {
            request.setAttribute("message", "Invalid book ID for update.");
            request.setAttribute("messageType", "danger");
            response.sendRedirect(request.getContextPath() + "/books");
            return;
        }

        if (title == null || title.isEmpty() || author == null || author.isEmpty()) {
            request.setAttribute("message", "Title and Author are required.");
            request.setAttribute("messageType", "danger");
            // To pre-fill the form with existing data when error occurs
            Book currentBook = bookDAO.getBookById(bookId);
            if(currentBook != null) {
                currentBook.setTitle(title); // Update with user's attempt
                currentBook.setAuthor(author);
                currentBook.setDescription(description);
            } else { // Should not happen if ID is valid
                currentBook = new Book(bookId, title, author, description, currentUser.getId(), null, 0.0);
            }
            request.setAttribute("bookToEdit", currentBook);
            request.getRequestDispatcher("/WEB-INF/views/edit-book.jsp").forward(request, response);
            return;
        }

        Book bookToUpdate = new Book();
        bookToUpdate.setId(bookId); // Set the ID for the update operation
        bookToUpdate.setTitle(title);
        bookToUpdate.setAuthor(author);
        bookToUpdate.setDescription(description);

        if (bookDAO.updateBook(bookToUpdate)) {
            request.setAttribute("message", "Book updated successfully!");
            request.setAttribute("messageType", "success");
            response.sendRedirect(request.getContextPath() + "/books"); // Redirect to book list
        } else {
            request.setAttribute("message", "Error updating book. Please try again.");
            request.setAttribute("messageType", "danger");
            request.setAttribute("bookToEdit", bookToUpdate); // Pre-fill form with attempted changes
            request.getRequestDispatcher("/WEB-INF/views/edit-book.jsp").forward(request, response);
        }
    }

    private void deleteBook(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            request.setAttribute("message", "Please log in to delete a book.");
            request.setAttribute("messageType", "danger");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User currentUser = (User) session.getAttribute("currentUser");
        int bookId;
        try {
            bookId = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            request.setAttribute("message", "Invalid book ID for deletion.");
            request.setAttribute("messageType", "danger");
            response.sendRedirect(request.getContextPath() + "/books");
            return;
        }

        Book bookToDelete = bookDAO.getBookById(bookId);
        if (bookToDelete == null || currentUser.getId() != bookToDelete.getAddedByUserId()) {
            request.setAttribute("message", "Book not found or you don't have permission to delete this book.");
            request.setAttribute("messageType", "danger");
            response.sendRedirect(request.getContextPath() + "/books");
            return;
        }

        if (bookDAO.deleteBook(bookId)) {
            request.setAttribute("message", "Book deleted successfully!");
            request.setAttribute("messageType", "success");
        } else {
            request.setAttribute("message", "Error deleting book. Please try again.");
            request.setAttribute("messageType", "danger");
        }
        response.sendRedirect(request.getContextPath() + "/books");
    }
}