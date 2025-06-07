<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MyBookCorner - Books</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            background-color: #f8f9fa;
        }
        .container {
            margin-top: 30px;
        }
        .book-card {
            margin-bottom: 20px;
            box-shadow: 0 4px 8px rgba(0,0,0,0.1);
            transition: transform 0.2s;
        }
        .book-card:hover {
            transform: translateY(-5px);
        }
        .card-body h5 {
            font-size: 1.25rem;
            margin-bottom: 0.5rem;
        }
        .card-text small {
            color: #6c757d;
        }
        .rating-stars {
            color: #ffc107; /* Yellow color for stars */
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
                        <a class="nav-link active" aria-current="page" href="<%= request.getContextPath() %>/books">Books</a>
                    </li>
                </ul>
                <ul class="navbar-nav ms-auto">
                    <%-- If user is logged in --%>
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
                    <%-- If user is not logged in --%>
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
        <h2 class="mb-4 text-center">Book List</h2>

        <%-- Search Bar --%>
        <form class="d-flex mb-4" action="<%= request.getContextPath() %>/books" method="get">
            <input class="form-control me-2" type="search" placeholder="Search by Title or Author" aria-label="Search" name="search">
            <button class="btn btn-outline-success" type="submit">Search</button>
        </form>

        <c:if test="${not empty requestScope.message}">
            <div class="alert alert-${requestScope.messageType == 'success' ? 'success' : 'danger'} alert-dismissible fade show" role="alert">
                <c:out value="${requestScope.message}"/>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>

        <div class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
            <c:choose>
                <c:when test="${not empty books}">
                    <c:forEach var="book" items="${books}">
                        <div class="col">
                            <div class="card h-100 book-card">
                                <div class="card-body">
                                    <h5 class="card-title"><a href="<%= request.getContextPath() %>/book-detail?id=<c:out value="${book.id}"/>" class="text-decoration-none text-dark"><c:out value="${book.title}"/></a></h5>
                                    <h6 class="card-subtitle mb-2 text-muted"><c:out value="${book.author}"/></h6>
                                    <p class="card-text">
                                        <small class="text-muted">
                                            <%-- Display average rating with stars --%>
                                            Average Rating:
                                            <c:set var="rating" value="${book.averageRating}" />
                                            <c:forEach begin="1" end="5" varStatus="loop">
                                                <c:choose>
                                                    <c:when test="${loop.index <= rating}">
                                                        <span class="rating-stars">&#9733;</span>
                                                    </c:when>
                                                    <c:when test="${loop.index - 1 < rating && loop.index > rating}">
                                                        <span class="rating-stars">&#9733;</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        &#9734; <%-- Empty star --%>
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:forEach>
                                            (<c:out value="${String.format('%.1f', book.averageRating)}"/>)
                                        </small>
                                    </p>
                                    <p class="card-text">${book.description != null && book.description.length() > 100 ? book.description.substring(0, 100).concat("...") : book.description}</p>
                                    <a href="<%= request.getContextPath() %>/book-detail?id=<c:out value="${book.id}"/>" class="btn btn-sm btn-primary">View Details</a>
                                    <c:if test="${sessionScope.currentUser != null && sessionScope.currentUser.id == book.addedByUserId}">
                                        <a href="<%= request.getContextPath() %>/edit-book?id=<c:out value="${book.id}"/>" class="btn btn-sm btn-info text-white">Edit</a>
                                        <a href="<%= request.getContextPath() %>/delete-book?id=<c:out value="${book.id}"/>" class="btn btn-sm btn-danger" onclick="return confirm('Are you sure you want to delete this book?');">Delete</a>
                                    </c:if>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <div class="col-12">
                        <div class="alert alert-info text-center" role="alert">
                            No books found. You can be the first to add one!
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>