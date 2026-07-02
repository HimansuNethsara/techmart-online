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
    <title>TechMart Online — My Orders</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body data-context-path="${pageContext.request.contextPath}">

<!-- Navigation Bar -->
<div class="navbar">
    <div class="brand">TechMart <span>Online</span></div>
    <nav>
        <a href="${pageContext.request.contextPath}/catalog">Catalog</a>
        <a href="${pageContext.request.contextPath}/cart">Cart</a>
        <a href="${pageContext.request.contextPath}/orders" class="active">Orders</a>
        <a href="${pageContext.request.contextPath}/logout">Logout</a>
    </nav>
</div>

<div class="container orders-container">
    <h1 class="page-title">📦 My Orders</h1>

    <% if (request.getAttribute("errorMessage") != null) { %>
    <div class="error-banner"><%= request.getAttribute("errorMessage") %></div>
    <% } %>

    <c:choose>
        <c:when test="${empty orders}">
            <div class="empty-state">
                <p>You haven't placed any orders yet.</p>
                <a href="${pageContext.request.contextPath}/catalog" class="btn">Start Shopping</a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="orders-grid">
                <c:forEach var="order" items="${orders}">
                    <div class="order-card">
                        <div class="order-header">
                            <div class="order-info">
                                <span class="order-id">Order #${order.orderId}</span>
                                <span class="order-date">
                                    <c:set var="createdAt" value="${order.createdAt}"/>
                                    ${createdAt.monthValue}/${createdAt.dayOfMonth}/${createdAt.year}
                                    ${createdAt.hour}:${createdAt.minute}
                                </span>
                            </div>
                            <span class="order-status status-${order.status}">${order.status}</span>
                        </div>

                        <div class="order-items">
                            <table class="order-items-table">
                                <thead>
                                <tr>
                                    <th>Product</th>
                                    <th>Quantity</th>
                                    <th>Price</th>
                                    <th>Total</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach var="item" items="${order.items}">
                                    <tr>
                                        <td>
                                            <div class="order-product">
                                                <c:choose>
                                                    <c:when test="${not empty item.product.imagePath}">
                                                        <img src="${pageContext.request.contextPath}/${item.product.imagePath}"
                                                             alt="${item.product.name}" class="order-product-img">
                                                    </c:when>
                                                    <c:otherwise>
                                                        <img src="${pageContext.request.contextPath}/images/products/placeholder.jpg"
                                                             alt="${item.product.name}" class="order-product-img">
                                                    </c:otherwise>
                                                </c:choose>
                                                <span class="order-product-name">${item.product.name}</span>
                                            </div>
                                        </td>
                                        <td>${item.quantity}</td>
                                        <td><fmt:formatNumber value="${item.unitPrice}" type="currency" currencySymbol="Rs. "/></td>
                                        <td><fmt:formatNumber value="${item.unitPrice * item.quantity}" type="currency" currencySymbol="Rs. "/></td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>

                        <div class="order-footer">
                            <div class="order-total">
                                <span class="total-label">Total Amount:</span>
                                <span class="total-value"><fmt:formatNumber value="${order.totalAmount}" type="currency" currencySymbol="Rs. "/></span>
                            </div>
                            <c:if test="${order.status == 'PENDING'}">
                                <form method="post" action="${pageContext.request.contextPath}/orders">
                                    <input type="hidden" name="orderId" value="${order.orderId}">
                                    <input type="hidden" name="action" value="cancel">
                                    <button type="submit" class="btn btn-danger btn-sm"
                                            onclick="return confirm('Are you sure you want to cancel this order?')">
                                        Cancel Order
                                    </button>
                                </form>
                            </c:if>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>