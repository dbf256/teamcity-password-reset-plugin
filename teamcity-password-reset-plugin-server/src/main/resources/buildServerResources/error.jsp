<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="header.jsp" />
<jsp:useBean id="error" type="java.lang.String" scope="request" />

<div class="rp_password_reset_div">
    <div class="rp_content_div">
        <h1 class="rp_h1">Password reset</h1>
        <span class="rp_error">${error}</span><br />
        <a href="<c:url value="passwordReset.html"/>"/>Click here</a> to reset password.
    </div>
</div>