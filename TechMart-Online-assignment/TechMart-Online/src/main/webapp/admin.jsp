<%--
  Created by IntelliJ IDEA.
  User: DRACULA
  Date: 6/22/2026
  Time: 11:07 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>TechMart Online — Admin Panel</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">

</head>
<body data-context-path="${pageContext.request.contextPath}">

<!-- Navigation Bar -->
<div class="navbar">
    <div class="brand">TechMart <span>Online</span></div>
    <nav>
        <a href="${pageContext.request.contextPath}/metrics-page">Metrics</a>
        <a href="${pageContext.request.contextPath}/admin-panel" class="active">Admin</a>
        <a href="${pageContext.request.contextPath}/admin-logout" class="admin-logout">Logout</a>
    </nav>
</div>

<div class="admin-container">

    <div class="particles" id="particles"></div>

    <div class="admin-header">
        <div class="header-content">
            <h1 class="glitch-text" data-text="ADMIN CONTROL">ADMIN CONTROL</h1>
            <p class="subtitle">⚡ System Management Interface ⚡</p>
            <div class="status-bar">
                <span class="status-indicator online"></span>
                <span>System Online</span>
                <span class="divider">|</span>
                <span id="liveTime">--:--:--</span>
                <span class="divider">|</span>
                <span>${sessionScope.admin.email}</span>
            </div>
        </div>
    </div>

    <div class="stats-grid">
        <div class="stat-card">
            <div class="stat-icon">📊</div>
            <div class="stat-info">
                <div class="stat-value" id="totalProducts">${totalProducts}</div>
                <div class="stat-label">Total Products</div>
            </div>
            <div class="stat-progress">
                <div class="progress-bar" style="width: ${totalProducts > 0 ? 85 : 0}%"></div>
            </div>
        </div>
        <div class="stat-card">
            <div class="stat-icon">📦</div>
            <div class="stat-info">
                <div class="stat-value" id="totalStock">${totalStock}</div>
                <div class="stat-label">Total Stock Units</div>
            </div>
            <div class="stat-progress">
                <div class="progress-bar" style="width: ${totalStock > 0 ? 60 : 0}%"></div>
            </div>
        </div>
        <div class="stat-card">
            <div class="stat-icon">⚠️</div>
            <div class="stat-info">
                <div class="stat-value" id="lowStockCount" style="color: ${lowStockCount > 0 ? '#ffaa44' : '#66ff88'}">${lowStockCount}</div>
                <div class="stat-label">Low Stock Items</div>
            </div>
            <div class="stat-progress">
                <div class="progress-bar" style="width: ${lowStockCount > 0 ? 45 : 0}%; background: ${lowStockCount > 0 ? '#ffaa44' : '#66ff88'}"></div>
            </div>
        </div>
        <div class="stat-card">
            <div class="stat-icon">👥</div>
            <div class="stat-info">
                <div class="stat-value">${totalCustomers}</div>
                <div class="stat-label">Total Customers</div>
            </div>
            <div class="stat-progress">
                <div class="progress-bar" style="width: ${totalCustomers > 0 ? 70 : 0}%"></div>
            </div>
        </div>
    </div>

    <div class="action-grid">
        <div class="action-card" data-action="metrics">
            <div class="action-icon">📈</div>
            <div class="action-content">
                <h3>Metrics Dashboard</h3>
                <p>View real-time metrics</p>
                <div class="action-footer">
                    <span class="action-tag">Analytics</span>
                    <a href="${pageContext.request.contextPath}/metrics-page" class="action-btn">
                        <span>Access</span>
                        <span class="btn-arrow">→</span>
                    </a>
                </div>
            </div>
            <div class="action-glow"></div>
        </div>

        <div class="action-card" data-action="add-product">
            <div class="action-icon">➕</div>
            <div class="action-content">
                <h3>Add New Product</h3>
                <p>Create and add new gaming products to the catalog</p>
                <div class="action-footer">
                    <span class="action-tag">Create</span>
                    <a href="${pageContext.request.contextPath}/admin/add-product" class="action-btn">
                        <span>Launch</span>
                        <span class="btn-arrow">→</span>
                    </a>
                </div>
            </div>
            <div class="action-glow"></div>
        </div>

        <div class="action-card" data-action="update-inventory">
            <div class="action-icon">🔄</div>
            <div class="action-content">
                <h3>Manage Products</h3>
                <p>View, search, update stock, and delete products</p>
                <div class="action-footer">
                    <span class="action-tag">Manage</span>
                    <a href="${pageContext.request.contextPath}/admin/manage-products" class="action-btn">
                        <span>Launch</span>
                        <span class="btn-arrow">→</span>
                    </a>
                </div>
            </div>
            <div class="action-glow"></div>
        </div>

        <div class="action-card" data-action="view-customers">
            <div class="action-icon">👥</div>
            <div class="action-content">
                <h3>View Customers</h3>
                <p>Browse and manage customer accounts</p>
                <div class="action-footer">
                    <span class="action-tag">Users</span>
                    <a href="${pageContext.request.contextPath}/admin/customers" class="action-btn">
                        <span>Launch</span>
                        <span class="btn-arrow">→</span>
                    </a>
                </div>
            </div>
            <div class="action-glow"></div>
        </div>

        <div class="action-card" data-action="settings">
            <div class="action-icon">⚙️</div>
            <div class="action-content">
                <h3>System Settings</h3>
                <p>Configure admin preferences and system parameters</p>
                <div class="action-footer">
                    <span class="action-tag">Config</span>
                    <button class="action-btn" onclick="showNotification('System Settings feature coming soon!')">
                        <span>Launch</span>
                        <span class="btn-arrow">→</span>
                    </button>
                </div>
            </div>
            <div class="action-glow"></div>
        </div>

        <div class="action-card" data-action="reports">
            <div class="action-icon">📊</div>
            <div class="action-content">
                <h3>Reports & Analytics</h3>
                <p>Generate detailed sales reports and performance analytics</p>
                <div class="action-footer">
                    <span class="action-tag">Analytics</span>
                    <button class="action-btn" onclick="showNotification('Reports & Analytics feature coming soon!')">
                        <span>Launch</span>
                        <span class="btn-arrow">→</span>
                    </button>
                </div>
            </div>
            <div class="action-glow"></div>
        </div>

        <div class="action-card" data-action="email-templates">
            <div class="action-icon">✉️</div>
            <div class="action-content">
                <h3>Email Templates</h3>
                <p>Manage and customize email notifications for customers</p>
                <div class="action-footer">
                    <span class="action-tag">Communication</span>
                    <button class="action-btn" onclick="showNotification('Email Templates feature coming soon!')">
                        <span>Launch</span>
                        <span class="btn-arrow">→</span>
                    </button>
                </div>
            </div>
            <div class="action-glow"></div>
        </div>

        <div class="action-card" data-action="backup">
            <div class="action-icon">💾</div>
            <div class="action-content">
                <h3>Backup & Restore</h3>
                <p>Manage database backups and system recovery options</p>
                <div class="action-footer">
                    <span class="action-tag">Security</span>
                    <button class="action-btn" onclick="showNotification('Backup & Restore feature coming soon!')">
                        <span>Launch</span>
                        <span class="btn-arrow">→</span>
                    </button>
                </div>
            </div>
            <div class="action-glow"></div>
        </div>

    </div>

    <div class="activity-log">
        <div class="log-header">
            <h3>📋 Recent Admin Activity</h3>
            <span class="log-status">Live</span>
        </div>
        <div class="log-container" id="adminActivityLog">
            <div class="log-entry">
                <span class="log-time">--:--:--</span>
                <span class="log-badge info">System</span>
                <span class="log-message">Admin panel initialized</span>
            </div>
        </div>
    </div>
</div>

<div class="notification-container" id="notificationContainer"></div>

<script src="${pageContext.request.contextPath}/js/admin.js"></script>
</body>
</html>