<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="header.jsp" />

<jsp:useBean id="error" type="java.lang.String" scope="request" />

<div class="rp_password_reset_div">
    <div class="rp_content_div">
        <h1 class="rp_h1">Password reset</h1>
        Enter your email and press "Reset password" to continue. <br />
        <c:if test="${not empty error}">
            <span class="rp_error">${error}</span>
        </c:if>

        <form action="passwordReset.html?action=sendMail" method="post">
            <table class="rp_table">
                <tbody>
                    <tr class="rp_form_field">
                      <td><label for="email" class="rp_label">Email:</label></td>
                      <td><input type="text" name="email" id="email" class="rp_text" size="50"></td>
                    </tr>
                </tbody>
            </table>
            <input type="submit" value="Reset password" name="submitLogin" class="rp_button">
        </form>
    </div>
</div>

<jsp:include page="footer.jsp" />