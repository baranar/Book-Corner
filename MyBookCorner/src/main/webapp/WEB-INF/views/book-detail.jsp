<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MyBookCorner - Book Details</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            background-color: #f8f9fa;
        }
        .container {
            margin-top: 30px;
        }
        .rating-stars {
            color: #ffc107; /* Yellow color for stars */
            font-size: 1.2rem;
        }
        .review-card {
            margin-bottom: 15px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.05);
        }
        .review-header {
            font-weight: bold;
            color: #343a40;
        }
        .review-timestamp {
            font-size: 0.85em;
            color: #6c757d;
        }
        .rating-input .star-rating label {
            font-size: 2rem;
            cursor: pointer;
            color: #ccc;
        }
        .rating-input .star-rating input:checked ~ label,
        .rating-input .star-rating label:hover,
        .rating-input .star-rating label:hover ~ label {
            color: #ffc107;
        }
        .rating-input .star-rating input {
            display: none;
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
        <c:if test="${book != null}">
            <h2 class="mb-4 text-center">Book Details</h2>

            <%-- Message Box --%>
            <c:if test="${not empty requestScope.message}">
                <div class="alert alert-${requestScope.messageType == 'success' ? 'success' : 'danger'} alert-dismissible fade show" role="alert">
                    <c:out value="${requestScope.message}"/>
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </c:if>

            <div class="card mb-4">
                <div class="card-body">
                    <h3 class="card-title">${book.title}</h3>
                    <h5 class="card-subtitle mb-3 text-muted">by ${book.author}</h5>
                    <p class="card-text">${book.description}</p>
                    <p class="card-text">
                        <small class="text-muted">
                            Average Rating:
                            <c:set var="rating" value="${book.averageRating}" />
                            <c:forEach begin="1" end="5" varStatus="loop">
                                <c:choose>
                                    <c:when test="${loop.index <= rating}">
                                        <span class="rating-stars">&#9733;</span> <%-- Filled star --%>
                                    </c:when>
                                    <c:when test="${loop.index - 1 < rating && loop.index > rating}">
                                        <span class="rating-stars">&#9733;</span> <%-- Half star --%>
                                    </c:when>
                                    <c:otherwise>
                                        &#9734; <%-- Empty star --%>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>
                            (<c:out value="${String.format('%.1f', book.averageRating)}"/>)
                        </small>
                    </p>
                    <a href="<%= request.getContextPath() %>/books" class="btn btn-secondary">Back to Book List</a>
                    <c:if test="${sessionScope.currentUser != null && sessionScope.currentUser.id == book.addedByUserId}">
                        <a href="<%= request.getContextPath() %>/edit-book?id=${book.id}" class="btn btn-info text-white">Edit Book</a>
                        <a href="<%= request.getContextPath() %>/delete-book?id=${book.id}" class="btn btn-danger" onclick="return confirm('Are you sure you want to delete this book?');">Delete Book</a>
                    </c:if>
                </div>
            </div>

            <h4 class="mb-3">Reviews</h4>

            <%-- Review Form (if user is logged in) --%>
            <c:if test="${sessionScope.currentUser != null}">
                <div class="card mb-4 p-3 rating-input">
                    <h5 class="card-title">Leave a Review</h5>
                    <form action="<%= request.getContextPath() %>/add-review" method="post">
                        <input type="hidden" name="bookId" value="${book.id}">
                        <div class="mb-3">
                            <label class="form-label">Your Rating:</label>
                            <div class="star-rating">
                                <input type="radio" id="star5" name="rating" value="5" <c:if test="${userReview != null && userReview.rating == 5}">checked</c:if> /><label for="star5" title="5 stars">&#9733;</label>
                                <input type="radio" id="star4" name="rating" value="4" <c:if test="${userReview != null && userReview.rating == 4}">checked</c:if> /><label for="star4" title="4 stars">&#9733;</label>
                                <input type="radio" id="star3" name="rating" value="3" <c:if test="${userReview != null && userReview.rating == 3}">checked</c:if> /><label for="star3" title="3 stars">&#9733;</label>
                                <input type="radio" id="star2" name="rating" value="2" <c:if test="${userReview != null && userReview.rating == 2}">checked</c:if> /><label for="star2" title="2 stars">&#9733;</label>
                                <input type="radio" id="star1" name="rating" value="1" <c:if test="${userReview != null && userReview.rating == 1}">checked</c:if> /><label for="star1" title="1 star">&#9733;</label>
                            </div>
                        </div>
                        <div class="mb-3">
                            <label for="comment" class="form-label">Your Comment:</label>
                            <textarea class="form-control" id="comment" name="comment" rows="3" placeholder="Write your thoughts about this book...">${userReview != null ? userReview.comment : ''}</textarea>
                        </div>
                        <button type="submit" class="btn btn-primary">Submit Review</button>
                    </form>
                </div>
            </c:if>
            <c:if test="${sessionScope.currentUser == null}">
                <div class="alert alert-info text-center" role="alert">
                    <a href="<%= request.getContextPath() %>/login">Log in</a> to leave a review and rating!
                </div>
            </c:if>

            <%-- List of Reviews --%>
            <div id="reviewsList">
                <c:choose>
                    <c:when test="${not empty reviews}">
                        <c:forEach var="review" items="${reviews}">
                            <div class="card review-card">
                                <div class="card-body">
                                    <h6 class="review-header mb-1">
                                        <c:out value="${review.username}"/>
                                        <c:forEach begin="1" end="${review.rating}" varStatus="loop">
                                            <span class="rating-stars">&#9733;</span>
                                        </c:forEach>
                                        <c:forEach begin="${review.rating + 1}" end="5" varStatus="loop">
                                            &#9734;
                                        </c:forEach>
                                    </h6>
                                    <p class="card-text mb-1">${review.comment}</p>
                                    <p class="review-timestamp text-end">Reviewed on: <c:out value="${review.createdAt}"/></p>
                                    <%-- Only show edit/delete if current user is the author of the review --%>
                                    <c:if test="${sessionScope.currentUser != null && sessionScope.currentUser.id == review.userId}">
                                        <a href="<%= request.getContextPath() %>/edit-review?id=${review.id}&bookId=${book.id}" class="btn btn-sm btn-info text-white">Edit Review</a>
                                        <a href="<%= request.getContextPath() %>/delete-review?id=${review.id}&bookId=${book.id}" class="btn btn-sm btn-danger" onclick="return confirm('Are you sure you want to delete this review?');">Delete Review</a>
                                    </c:if>
                                </div>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <div class="alert alert-warning text-center" role="alert">
                            No reviews yet for this book. Be the first to review!
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

        </c:if>
        <c:if test="${book == null}">
            <div class="alert alert-danger text-center mt-5" role="alert">
                Book not found!
            </div>
            <div class="text-center">
                <a href="<%= request.getContextPath() %>/books" class="btn btn-primary">Back to Book List</a>
            </div>
        </c:if>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>