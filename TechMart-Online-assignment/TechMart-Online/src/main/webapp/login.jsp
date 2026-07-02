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
    <title>TechMart Online — Login</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body data-context-path="${pageContext.request.contextPath}">

<div class="login-wrapper">
    <div class="login-card">
        <h1>TechMart <span style="color:#ff3333;">Online</span></h1>
        <p class="subtitle">Consoles · Gaming Hardware · VR</p>

        <% if (request.getAttribute("errorMessage") != null) { %>
        <div class="error-banner"><%= request.getAttribute("errorMessage") %></div>
        <% } %>

        <form method="post" action="${pageContext.request.contextPath}/login">
            <div class="field">
                <label for="email">Email</label>
                <input type="email" id="email" name="email" required autofocus>
            </div>
            <div class="field">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" required>
            </div>
            <button type="submit" class="btn btn-block">Sign In</button>
        </form>

        <div class="login-footer">
            <p>Don't have an account? <a href="${pageContext.request.contextPath}/signup" class="signup-link">Create Account</a></p>
        </div>
    </div>
</div>

<script>
    (function() {
        'use strict';

        const contextPath = document.body.getAttribute('data-context-path') || '';

        // Handle signup link click with smooth transition
        const signupLink = document.querySelector('.signup-link');
        if (signupLink) {
            signupLink.addEventListener('click', function(e) {
                e.preventDefault();
                const targetUrl = this.getAttribute('href');

                // Add a fade-out effect before navigation
                const loginCard = document.querySelector('.login-card');
                if (loginCard) {
                    loginCard.style.transition = 'opacity 0.3s ease, transform 0.3s ease';
                    loginCard.style.opacity = '0';
                    loginCard.style.transform = 'translateY(-10px)';
                }

                setTimeout(function() {
                    window.location.href = targetUrl;
                }, 300);
            });
        }

        // Add keyboard shortcut: Ctrl+Shift+S to go to signup
        document.addEventListener('keydown', function(e) {
            if (e.ctrlKey && e.shiftKey && (e.key === 'S' || e.key === 's')) {
                e.preventDefault();
                window.location.href = contextPath + '/signup';
            }
        });

        const signupLinkHover = document.querySelector('.signup-link');
        if (signupLinkHover) {
            signupLinkHover.addEventListener('mouseenter', function() {
                this.style.transition = 'all 0.3s ease';
                this.style.textShadow = '0 0 10px rgba(255, 51, 51, 0.3)';
            });

            signupLinkHover.addEventListener('mouseleave', function() {
                this.style.textShadow = 'none';
            });
        }

        const emailField = document.getElementById('email');
        if (emailField && !emailField.value) {
            setTimeout(function() {
                emailField.focus();
            }, 100);
        }

        const loginForm = document.querySelector('form[action*="login"]');
        if (loginForm) {
            loginForm.addEventListener('submit', function(e) {
                const submitBtn = this.querySelector('.btn-block');
                if (submitBtn) {
                    submitBtn.textContent = 'Signing In...';
                    submitBtn.disabled = true;
                    submitBtn.style.opacity = '0.7';
                    submitBtn.style.cursor = 'not-allowed';
                }
            });
        }

        console.log('TechMart Login page loaded. Press Ctrl+Shift+S to go to Signup.');
    })();
</script>
</body>
</html>