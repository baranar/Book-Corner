<%-- This JSP serves as a redirect to the /books servlet --%>
<%
    // Redirect to the /books servlet when the application's root URL is accessed.
    response.sendRedirect(request.getContextPath() + "/books");
%>