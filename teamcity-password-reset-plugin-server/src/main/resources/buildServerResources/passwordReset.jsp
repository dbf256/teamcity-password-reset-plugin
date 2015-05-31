<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="header.jsp" />

<jsp:useBean id="token" type="java.lang.String" scope="request" />
<jsp:useBean id="error" type="java.lang.String" scope="request" />

<div class="rp_password_reset_div">
    <div class="rp_content_div">
        <h1 class="rp_h1">Password reset</h1>
        Please enter new password and repeat it.<br />
        <c:if test="${not empty error}">
            <span class="rp_error">${error}</span>
        </c:if>
        <form action="passwordReset.html?action=setPassword" method="post">
            <table class="rp_table">
                <tbody>
                    <tr class="rp_form_field">
                      <td><label for="password1" class="rp_label">New password:</label></td>
                      <td><input type="password" name="password1" id="password1" class="rp_text" type="password" size="50"></td>
                    </tr>
                    <tr class="rp_form_field">
                      <td><label for="password2" class="rp_label">New password (repeat):</label></td>
                      <td><input type="password" name="password2" id="password2" class="rp_text" type="password" size="50"></td>
                    </tr>
                    <input type="hidden" name="token" value="${token}"/>
                </tbody>
            </table>
            <input type="submit" value="Set new password" name="submitLogin" class="rp_button">
        </form>
    </div>
</div>

<jsp:include page="footer.jsp" />