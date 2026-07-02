<%--
  Created by IntelliJ IDEA.
  User: DRACULA
  Date: 6/22/2026
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>TechMart Online — Customers</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-customers.css">
</head>
<body data-context-path="${pageContext.request.contextPath}">

<div class="navbar">
    <div class="brand">TechMart <span>Online</span></div>
    <nav>
        <a href="${pageContext.request.contextPath}/metrics-page">Metrics</a>
        <a href="${pageContext.request.contextPath}/admin-panel">Admin</a>
        <a href="${pageContext.request.contextPath}/admin-logout">Logout</a>
    </nav>
</div>

<div class="admin-customers-container">
    <div class="customers-wrapper">
        <div class="customers-header">
            <h1 class="glitch-text" data-text="CUSTOMERS">👥 CUSTOMERS</h1>
            <p class="subtitle">Manage and view customer accounts</p>
        </div>

        <% if (request.getAttribute("errorMessage") != null) { %>
        <div class="error-banner admin-error">
            <span class="error-icon">⚠️</span>
            <%= request.getAttribute("errorMessage") %>
        </div>
        <% } %>

        <!-- Stats Row -->
        <div class="stats-row">
            <div class="stat-mini">
                <span class="stat-mini-value">${totalCustomers}</span>
                <span class="stat-mini-label">Total Customers</span>
            </div>
            <div class="stat-mini">
                <span class="stat-mini-value">${customers.size()}</span>
                <span class="stat-mini-label">Showing</span>
            </div>
        </div>

        <!-- Search and Toolbar -->
        <div class="customers-toolbar">
            <div class="search-wrapper">
                <input type="text" id="searchInput" placeholder="🔍 Search by name or email..."
                       class="search-input" value="${searchTerm}">
                <button onclick="searchCustomers()" class="btn btn-primary btn-sm">Search</button>
                <button onclick="clearSearch()" class="btn btn-secondary btn-sm">Clear</button>
            </div>
            <a href="${pageContext.request.contextPath}/admin-panel" class="btn btn-secondary">
                ← Back to Admin
            </a>
        </div>

        <!-- Customers Table -->
        <div class="table-responsive">
            <table class="customers-table">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Full Name</th>
                    <th>Email</th>
                    <th>Registered</th>
                    <th>Status</th>
                </tr>
                </thead>
                <tbody id="customersTableBody">
                <c:forEach var="customer" items="${customers}">
                    <tr>
                        <td class="customer-id">#${customer.customerId}</td>
                        <td class="customer-name">${customer.firstName} ${customer.lastName}</td>
                        <td class="customer-email">${customer.email}</td>
                        <td class="customer-date">
                            <c:set var="createdAt" value="${customer.createdAt}"/>
                                ${createdAt.monthValue}/${createdAt.dayOfMonth}/${createdAt.year} ${createdAt.hour}:${createdAt.minute}
                        </td>
                        <td>
                            <span class="status-badge status-active">Active</span>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>

        <!-- Empty State -->
        <div id="emptyState" class="empty-state" style="display: ${empty customers ? 'block' : 'none'}">
            <p>No customers found.</p>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/admin.js"></script>
<script src="${pageContext.request.contextPath}/js/admin-customers.js"></script>
</body>
</html>