<%--
  Created by IntelliJ IDEA.
  User: DRACULA
  Date: 6/22/2026
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>TechMart Online — Add Product</title>
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
            <h1 class="glitch-text" data-text="ADD PRODUCT">➕ ADD PRODUCT</h1>
            <p class="subtitle">Add new gaming products to the catalog</p>
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

        <form method="post" action="${pageContext.request.contextPath}/admin/add-product"
              enctype="multipart/form-data" id="addProductForm">

            <div class="form-grid">
                <div class="form-group">
                    <label for="categoryId">Category *</label>
                    <select id="categoryId" name="categoryId" required>
                        <option value="">Select Category</option>
                        <c:forEach var="category" items="${categories}">
                            <option value="${category.categoryId}">${category.name}</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label for="name">Product Name *</label>
                    <input type="text" id="name" name="name" required
                           placeholder="e.g., Gaming Mouse X10" maxlength="100">
                </div>

                <div class="form-group">
                    <label for="brand">Brand *</label>
                    <input type="text" id="brand" name="brand" required
                           placeholder="e.g., Razer" maxlength="50">
                </div>

                <div class="form-group">
                    <label for="basePrice">Base Price ($) *</label>
                    <input type="number" id="basePrice" name="basePrice" required
                           step="0.01" min="0.01" placeholder="0.00">
                </div>

                <div class="form-group full-width">
                    <label for="description">Description</label>
                    <textarea id="description" name="description" rows="4"
                              placeholder="Product description..."></textarea>
                </div>

                <div class="form-group full-width">
                    <label for="imageFile">Product Image</label>
                    <div class="file-upload-wrapper">
                        <input type="file" id="imageFile" name="imageFile"
                               accept="image/*" class="file-input">
                        <label for="imageFile" class="file-label">
                            <span class="file-icon">📁</span>
                            <span class="file-text">Choose Image File</span>
                        </label>
                        <span class="file-name" id="fileName">No file selected</span>
                    </div>
                </div>

                <!-- Inventory Section -->
                <div class="form-group full-width inventory-section">
                    <h3 class="section-title">📦 Initial Inventory Setup</h3>
                    <p class="section-hint">Add stock to warehouses for this product</p>

                    <div class="inventory-grid">
                        <div class="form-group">
                            <label for="warehouseId">Select Warehouse *</label>
                            <select id="warehouseId" name="warehouseId" required>
                                <option value="">Select Warehouse</option>
                                <c:forEach var="warehouse" items="${warehouses}">
                                    <option value="${warehouse.warehouseId}">
                                            ${warehouse.name} (${warehouse.region})
                                    </option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="form-group">
                            <label for="stockQuantity">Initial Stock Quantity</label>
                            <input type="number" id="stockQuantity" name="stockQuantity"
                                   min="0" value="0" placeholder="0">
                        </div>

                        <div class="form-group">
                            <label for="reorderThreshold">Reorder Threshold</label>
                            <input type="number" id="reorderThreshold" name="reorderThreshold"
                                   min="1" value="5" placeholder="5">
                            <small class="form-hint">Alert when stock falls below this number</small>
                        </div>
                    </div>

                    <!-- Option to add more warehouse entries -->
                    <button type="button" class="btn-add-warehouse" onclick="addWarehouseRow()">
                        ➕ Add Another Warehouse
                    </button>
                </div>

                <!-- Dynamic warehouse rows will be added here -->
                <div id="additionalWarehouses"></div>
            </div>

            <div class="form-actions">
                <button type="submit" class="btn btn-primary">
                    <span class="btn-icon">➕</span>
                    Add Product
                </button>
                <a href="${pageContext.request.contextPath}/admin-panel" class="btn btn-secondary">
                    Cancel
                </a>
            </div>
        </form>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/admin.js"></script>
</body>
</html>