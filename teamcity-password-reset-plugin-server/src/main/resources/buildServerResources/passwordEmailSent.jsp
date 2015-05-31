<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="header.jsp" />

<jsp:useBean id="email" type="java.lang.String" scope="request" />

<div class="rp_password_reset_div">
    <div class="rp_content_div">
        <h1 class="rp_h1">Password reset</h1>
        <c:choose>
            <c:when test="${not empty error}">
                <span class="rp_error">${error}</span>
            </c:when>
            <c:otherwise>
                Password reset email was send to ${email}. Follow the link from email to reset password.
            </c:otherwise>
        </c:choose>
    </div>
</div>

<jsp:include page="footer.jsp" />