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
    <title>TechMart Online — Manage Products</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-product.css">
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

<div class="admin-product-container">
    <div class="product-form-wrapper">
        <div class="form-header">
            <h1 class="glitch-text" data-text="MANAGE PRODUCTS">📦 MANAGE PRODUCTS</h1>
            <p class="subtitle">View, search, update stock, and delete products</p>
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

        <!-- Search and Toolbar -->
        <div class="manage-toolbar">
            <div class="search-wrapper">
                <input type="text" id="searchInput" placeholder="🔍 Search products by name, brand..."
                       class="search-input">
                <button onclick="searchProducts()" class="btn btn-primary btn-sm">Search</button>
                <button onclick="clearSearch()" class="btn btn-secondary btn-sm">Clear</button>
            </div>
            <a href="${pageContext.request.contextPath}/admin/add-product" class="btn btn-primary">
                <span class="btn-icon">➕</span> Add New Product
            </a>
        </div>

        <!-- Products Table -->
        <div class="table-responsive">
            <table class="product-table" id="productTable">
                <thead>
                <tr>
                    <th>Image</th>
                    <th>Name</th>
                    <th>Brand</th>
                    <th>Category</th>
                    <th>Price</th>
                    <th>Stock</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody id="productTableBody">
                <c:forEach var="product" items="${products}">
                    <tr data-product-id="${product.productId}" data-name="${product.name}" data-brand="${product.brand}">
                        <td>
                            <c:choose>
                                <c:when test="${not empty product.imagePath}">
                                    <img src="${pageContext.request.contextPath}/${product.imagePath}"
                                         alt="${product.name}" class="product-thumbnail">
                                </c:when>
                                <c:otherwise>
                                    <img src="${pageContext.request.contextPath}/images/products/placeholder.jpg"
                                         alt="${product.name}" class="product-thumbnail">
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td class="product-name">${product.name}</td>
                        <td>${product.brand}</td>
                        <td>${product.category.name}</td>
                        <td><fmt:formatNumber value="${product.basePrice}" type="currency" currencySymbol="Rs. "/></td>
                        <td>
                            <c:set var="stock" value="${stockLevels[product.productId]}"/>
                            <span class="stock-badge ${stock <= 5 ? 'stock-low' : 'stock-ok'}">
                                    ${stock} units
                                </span>
                        </td>
                        <td>
                                <span class="status-badge ${product.active ? 'status-active' : 'status-inactive'}">
                                        ${product.active ? 'Active' : 'Inactive'}
                                </span>
                        </td>
                        <td>
                            <button onclick="openStockModal(${product.productId}, '${product.name}', ${stock})"
                                    class="btn btn-sm btn-stock" title="Update Stock">
                                📦 Stock
                            </button>
                            <button onclick="toggleProductStatus(${product.productId}, '${product.name}', ${product.active})"
                                    class="btn btn-sm ${product.active ? 'btn-deactivate' : 'btn-activate'}"
                                    title="${product.active ? 'Deactivate Product' : 'Activate Product'}">
                                    ${product.active ? '🔴 Deactivate' : '🟢 Activate'}
                            </button>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>

        <!-- Empty State -->
        <div id="emptyState" class="empty-state" style="display: none;">
            No products found. <a href="${pageContext.request.contextPath}/admin/add-product">Add a new product</a>
        </div>
    </div>
</div>

<!-- Stock Update Modal -->
<div id="stockModal" class="modal">
    <div class="modal-content">
        <div class="modal-header">
            <h2>📦 Update Stock</h2>
            <button class="modal-close" onclick="closeStockModal()">&times;</button>
        </div>
        <div class="modal-body">
            <p>Product: <strong id="modalProductName"></strong></p>
            <p>Current Stock: <strong id="modalCurrentStock"></strong> units</p>
            <div class="modal-form-group">
                <label for="modalStockQuantity">New Stock Quantity:</label>
                <input type="number" id="modalStockQuantity" min="0" value="0">
            </div>
        </div>
        <div class="modal-footer">
            <button onclick="updateStock()" class="btn btn-primary">💾 Update Stock</button>
            <button onclick="closeStockModal()" class="btn btn-secondary">Cancel</button>
        </div>
    </div>
</div>

<!-- Toggle Status Confirmation Modal -->
<div id="deleteModal" class="modal">
    <div class="modal-content delete-modal">
        <div class="modal-header">
            <h2 style="color: #ff3333;">⚠️ Confirm Status Change</h2>
            <button class="modal-close" onclick="closeDeleteModal()">&times;</button>
        </div>
        <div class="modal-body">
            <div class="delete-warning-icon" id="statusIcon">🔄</div>
            <p>Are you sure you want to <strong id="statusAction">deactivate</strong> <strong id="deleteProductName"></strong>?</p>
            <p class="delete-warning-text" id="statusWarning">This will hide the product from the catalog. You can activate it later.</p>
            <div class="delete-confirm-group">
                <label>
                    <input type="checkbox" id="deleteConfirmCheck">
                    I understand this action will change the product's visibility
                </label>
            </div>
        </div>
        <div class="modal-footer">
            <button onclick="confirmDelete()" class="btn btn-danger" id="confirmDeleteBtn" disabled>
                <span id="actionBtnText">🔄 Toggle Status</span>
            </button>
            <button onclick="closeDeleteModal()" class="btn btn-secondary">Cancel</button>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/admin.js"></script>
<script src="${pageContext.request.contextPath}/js/admin-manage-products.js"></script>
</body>
</html>