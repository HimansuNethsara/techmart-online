<%--
  Created by IntelliJ IDEA.
  User: DRACULA
  Date: 6/22/2026
  Time: 11:33 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>TechMart Online — Catalog</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body data-context-path="${pageContext.request.contextPath}">

<div class="navbar">
    <div class="brand">TechMart <span>Online</span></div>
    <nav>
        <a href="${pageContext.request.contextPath}/catalog">Catalog</a>
        <a href="${pageContext.request.contextPath}/cart">Cart</a>
        <a href="${pageContext.request.contextPath}/orders">Orders</a>
        <a href="${pageContext.request.contextPath}/logout">Logout</a>
    </nav>
</div>

<div class="container has-activity-panel">
    <h1 class="page-title">Product Catalog</h1>

    <form class="toolbar" method="get" action="${pageContext.request.contextPath}/catalog">
        <input type="text" name="keyword" placeholder="Search products..." value="${keyword}">
        <button type="submit" class="btn">Search</button>
        <a href="${pageContext.request.contextPath}/catalog" class="btn btn-secondary">Reset</a>
    </form>

    <c:choose>
        <c:when test="${empty products}">
            <div class="empty-state">No products found.</div>
        </c:when>
        <c:otherwise>
            <div class="product-grid">
                <c:forEach var="p" items="${products}">
                    <div class="product-card">
                        <c:choose>
                            <c:when test="${not empty p.imagePath}">
                                <img src="${pageContext.request.contextPath}/${p.imagePath}" alt="${p.name}">
                            </c:when>
                            <c:otherwise>
                                <img src="${pageContext.request.contextPath}/images/products/placeholder.jpg" alt="${p.name}">
                            </c:otherwise>
                        </c:choose>

                        <div class="name">${p.name}</div>
                        <div class="brand">${p.brand}</div>
                        <div class="price">
                            <fmt:formatNumber value="${p.basePrice}" type="currency" currencySymbol="Rs. "/>
                        </div>

                        <c:set var="qty" value="${stockLevels[p.productId]}"/>
                        <c:choose>
                            <c:when test="${lowStockFlags[p.productId]}">
                                <span class="stock-pill stock-low">Low stock: ${qty} left</span>
                            </c:when>
                            <c:otherwise>
                                <span class="stock-pill stock-ok">${qty} in stock</span>
                            </c:otherwise>
                        </c:choose>

                        <form method="post" action="${pageContext.request.contextPath}/cart" class="qty-row">
                            <input type="hidden" name="action" value="add">
                            <input type="hidden" name="productId" value="${p.productId}">
                            <button type="button" class="btn btn-secondary qty-decrease">−</button>
                            <input type="number" name="quantity" value="1" min="1">
                            <button type="button" class="btn btn-secondary qty-increase">+</button>
                            <button type="submit" class="btn">Add</button>
                        </form>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<div class="activity-panel">
    <h3><span class="pulse-dot"></span> System Activity</h3>
    <div id="activity-list">
        <div class="activity-empty">Loading...</div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>
