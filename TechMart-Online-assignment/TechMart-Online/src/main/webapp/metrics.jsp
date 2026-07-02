<%--
  Created by IntelliJ IDEA.
  User: DRACULA
  Date: 6/24/2026
  Time: 5:39 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>TechMart Online — Performance Metrics</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body data-context-path="${pageContext.request.contextPath}">

<div class="navbar">
    <div class="brand">TechMart <span>Online</span></div>
    <nav>
        <a href="${pageContext.request.contextPath}/metrics-page" class="active">Metrics</a>
        <a href="${pageContext.request.contextPath}/admin-panel">Admin</a>
        <a href="${pageContext.request.contextPath}/admin-logout" class="admin-logout">Logout</a>
    </nav>
</div>

<div class="container">
    <h1 class="page-title">Performance Metrics</h1>
    <p style="color:#8a8fa3; font-size:13px; margin-bottom:24px;">
        Live, in-memory counters collected by PerformanceMonitoringFilter across every request
        since this server instance started. Auto-refreshes every 3 seconds.
    </p>

    <div class="metrics-grid" id="summary-cards">
        <div class="metric-card">
            <div class="value" id="m-total">—</div>
            <div class="label">Total Requests</div>
        </div>
        <div class="metric-card">
            <div class="value" id="m-avg">—</div>
            <div class="label">Avg Response (ms)</div>
        </div>
        <div class="metric-card">
            <div class="value" id="m-slowest">—</div>
            <div class="label">Slowest Request (ms)</div>
        </div>
        <div class="metric-card error-card">
            <div class="value" id="m-errors">—</div>
            <div class="label">Errors (4xx/5xx)</div>
        </div>
    </div>

    <div class="section-subtitle">By Endpoint</div>
    <table class="metrics-table">
        <thead>
        <tr>
            <th>Endpoint</th>
            <th>Requests</th>
            <th>Avg (ms)</th>
            <th>Max (ms)</th>
        </tr>
        </thead>
        <tbody id="endpoint-rows">
        <tr><td colspan="4" style="color:#6b7080;">Loading...</td></tr>
        </tbody>
    </table>

    <div class="section-subtitle">Recent Requests</div>
    <table class="metrics-table">
        <thead>
        <tr>
            <th>Time</th>
            <th>Endpoint</th>
            <th>Duration (ms)</th>
            <th>Status</th>
        </tr>
        </thead>
        <tbody id="recent-rows">
        <tr><td colspan="4" style="color:#6b7080;">Loading...</td></tr>
        </tbody>
    </table>
</div>

<script src="${pageContext.request.contextPath}/js/app.js"></script>
<script src="${pageContext.request.contextPath}/js/metrics.js"></script>
</body>
</html>

