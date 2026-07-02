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
    <title>TechMart Online — Admin Login</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-login.css">
</head>
<body data-context-path="${pageContext.request.contextPath}">

<div class="admin-login-wrapper">
    <div class="admin-login-container">
        <!-- Animated Background -->
        <div class="admin-login-bg">
            <div class="grid-lines"></div>
            <div class="scan-line"></div>
        </div>

        <div class="admin-login-card">
            <div class="login-header">
                <div class="shield-icon">🛡Techmart Online</div>
                <h1>Admin <span>Access</span></h1>
                <p class="subtitle">🔐 Secure Management Portal</p>
            </div>

            <% if (request.getAttribute("errorMessage") != null) { %>
            <div class="error-banner admin-error">
                <span class="error-icon">⚠️</span>
                <%= request.getAttribute("errorMessage") %>
            </div>
            <% } %>

            <% if (request.getAttribute("successMessage") != null) { %>
            <div class="success-banner admin-success">
                <span class="success-icon">✅</span>
                <%= request.getAttribute("successMessage") %>
            </div>
            <% } %>

            <form method="post" action="${pageContext.request.contextPath}/admin-login" id="adminLoginForm">
                <div class="field">
                    <label for="email">Admin Email</label>
                    <div class="input-group">
                        <span class="input-icon">📧</span>
                        <input type="email" id="email" name="email" required autofocus
                               placeholder="admin@techmart.com">
                    </div>
                </div>
                <div class="field">
                    <label for="password">Password</label>
                    <div class="input-group">
                        <span class="input-icon">🔒</span>
                        <input type="password" id="password" name="password" required
                               placeholder="Enter your password">
                        <button type="button" class="toggle-password" id="togglePassword"
                                aria-label="Toggle password visibility">
                            👁️
                        </button>
                    </div>
                </div>
                <button type="submit" class="btn btn-block admin-login-btn">
                    <span class="btn-text">Authenticate</span>
                    <span class="btn-loader" style="display: none;">⏳</span>
                </button>
            </form>

            <div class="admin-login-footer">
                <div class="security-badge">
                    <span>🔒</span>
                    <span>Secured Connection</span>
                </div>
                <a href="${pageContext.request.contextPath}/login" class="back-link">
                    ← Return to Customer Login
                </a>
            </div>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/admin-login.js"></script>
</body>
</html>