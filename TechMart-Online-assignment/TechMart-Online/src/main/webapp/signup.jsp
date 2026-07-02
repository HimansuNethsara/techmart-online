<%--
  Created by IntelliJ IDEA.
  User: DRACULA
  Date: 6/22/2026
  Time: 11:07 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <meta charset="UTF-8">
        <title>TechMart Online — Sign Up</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    </head>
    <body data-context-path="${pageContext.request.contextPath}">

    <div class="login-wrapper">
        <div class="login-card signup-card">
            <h1>TechMart <span style="color:#ff3333;">Online</span></h1>
            <p class="subtitle">Create your account</p>

            <% if (request.getAttribute("errorMessage") != null) { %>
            <div class="error-banner"><%= request.getAttribute("errorMessage") %></div>
            <% } %>

            <% if (request.getAttribute("successMessage") != null) { %>
            <div class="success-banner"><%= request.getAttribute("successMessage") %></div>
            <% } %>

            <form method="post" action="${pageContext.request.contextPath}/signup">
                <div class="field">
                    <label for="fullName">Full Name</label>
                    <input type="text" id="fullName" name="fullName" required autofocus
                           placeholder="Lord Dracula">
                </div>
                <div class="field">
                    <label for="email">Email</label>
                    <input type="email" id="email" name="email" required
                           placeholder="dracula@example.com">
                </div>
                <div class="field">
                    <label for="password">Password</label>
                    <input type="password" id="password" name="password" required
                           placeholder="Min 8 characters">
                    <span class="password-hint">Must be at least 8 characters</span>
                </div>
                <div class="field">
                    <label for="confirmPassword">Confirm Password</label>
                    <input type="password" id="confirmPassword" name="confirmPassword" required
                           placeholder="Re-enter your password">
                </div>
                <button type="submit" class="btn btn-block">Create Account</button>
            </form>

            <div class="login-footer">
                <p>Already have an account? <a href="${pageContext.request.contextPath}/login">Sign In</a></p>
            </div>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/js/signup.js"></script>
    </body>
</html>