<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="header.jsp" />

<div class="rp_password_reset_div">
    <div class="rp_content_div">
        <h1 class="rp_h1">Password reset</h1>
        New password is set, <a href="<c:url value="login.html"/>"/>click to login</a>.
    </div>
</div>

<jsp:include page="footer.jsp" />