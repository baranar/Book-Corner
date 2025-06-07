<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MyBookCorner - Edit Book</title> <%-- Title updated --%>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            background-color: #f8f9fa;
        }
        .container {
            margin-top: 50px;
            max-width: 600px;
            padding: 30px;
            background-color: #ffffff;
            border-radius: 8px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }
        .form-control:focus, .form-control-file:focus {
            box-shadow: none;
            border-color: #0d6efd;
        }
    </style>
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <div class="container-fluid">
            <a class="navbar-brand" href="<%= request.getContextPath() %>/books">MyBookCorner</a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                    <li class="nav-item">
                        <a class="nav-link" href="<%= request.getContextPath() %>/books">Books</a>
                    </li>
                </ul>
                <ul class="navbar-nav ms-auto">
                    <c:if test="${sessionScope.currentUser != null}">
                        <li class="nav-item">
                            <span class="nav-link text-white">Welcome, **<c:out value="${sessionScope.currentUser.username}"/>**</span>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link btn btn-outline-light me-2" href="<%= request.getContextPath() %>/add-book">Add New Book</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link btn btn-outline-light" href="<%= request.getContextPath() %>/logout">Logout</a>
                        </li>
                    </c:if>
                    <c:if test="${sessionScope.currentUser == null}">
                        <li class="nav-item">
                            <a class="nav-link btn btn-outline-light me-2" href="<%= request.getContextPath() %>/login">Login</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link btn btn-primary" href="<%= request.getContextPath() %>/register">Register</a>
                        </li>
                    </c:if>
                </ul>
            </div>
        </div>
    </nav>

    <div class="container">
        <h2 class="mb-4 text-center">Edit Book</h2>

        <%-- Message Box --%>
        <c:if test="${not empty requestScope.message}">
            <div class="alert alert-${requestScope.messageType == 'success' ? 'success' : 'danger'} alert-dismissible fade show" role="alert">
                <c:out value="${requestScope.message}"/>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>

        <c:if test="${bookToEdit != null}"> <%-- Check if book object exists --%>
            <form action="<%= request.getContextPath() %>/edit-book" method="post">
                <%-- This is the crucial part: sending the book ID --%>
                <input type="hidden" name="id" value="${bookToEdit.id}">

                <div class="mb-3">
                    <label for="title" class="form-label">Book Title</label>
                    <input type="text" class="form-control" id="title" name="title" required value="${bookToEdit.title}">
                </div>
                <div class="mb-3">
                    <label for="author" class="form-label">Author Name</label>
                    <input type="text" class="form-control" id="author" name="author" required value="${bookToEdit.author}">
                </div>
                <div class="mb-3">
                    <label for="description" class="form-label">Description</label>
                    <textarea class="form-control" id="description" name="description" rows="5">${bookToEdit.description}</textarea>
                </div>
                <div class="d-grid gap-2">
                    <button type="submit" class="btn btn-primary">Update Book</button> <%-- Button text updated --%>
                    <a href="<%= request.getContextPath() %>/books" class="btn btn-secondary">Cancel</a>
                </div>
            </form>
        </c:if>
        <c:if test="${bookToEdit == null}">
            <div class="alert alert-danger text-center" role="alert">
                Book not found or you don't have permission to edit it.
            </div>
            <div class="text-center">
                <a href="<%= request.getContextPath() %>/books" class="btn btn-primary">Back to Book List</a>
            </div>
        </c:if>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>