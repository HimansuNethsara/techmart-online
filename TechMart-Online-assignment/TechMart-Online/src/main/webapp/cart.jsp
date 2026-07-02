<%--
  Created by IntelliJ IDEA.
  User: DRACULA
  Date: 6/22/2026
  Time: 11:16 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>TechMart Online — Cart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body data-context-path="${pageContext.request.contextPath}">

<div class="navbar">
    <div class="brand">TechMart <span>Online</span></div>
    <nav>
        <a href="${pageContext.request.contextPath}/catalog">Catalog</a>
        <a href="${pageContext.request.contextPath}/cart" class="active">Cart</a>
        <a href="${pageContext.request.contextPath}/orders">Orders</a>
        <a href="${pageContext.request.contextPath}/logout">Logout</a>
    </nav>
</div>

<div class="container has-activity-panel">
    <h1 class="page-title">Your Cart</h1>

    <c:if test="${not empty checkoutOrderId}">
        <div class="success-banner">
            Order #${checkoutOrderId} placed successfully! A confirmation notification
            is being sent asynchronously — check the System Activity panel shortly.
        </div>
    </c:if>

    <c:if test="${not empty errorMessage}">
        <div class="error-banner">${errorMessage}</div>
    </c:if>

    <c:choose>
        <c:when test="${empty items}">
            <div class="empty-state">Your cart is empty. <a href="${pageContext.request.contextPath}/catalog">Browse products</a>.</div>
        </c:when>
        <c:otherwise>
            <table class="cart-table">
                <thead>
                <tr>
                    <th>Product</th>
                    <th>Unit Price</th>
                    <th>Quantity</th>
                    <th>Subtotal</th>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="item" items="${items}">
                    <tr>
                        <td>${item.product.name}</td>
                        <td><fmt:formatNumber value="${item.product.basePrice}" type="currency" currencySymbol="Rs. "/></td>
                        <td>
                            <form method="post" action="${pageContext.request.contextPath}/cart" style="display:flex; gap:6px;">
                                <input type="hidden" name="action" value="update">
                                <input type="hidden" name="cartItemId" value="${item.cartItemId}">
                                <input type="number" name="quantity" value="${item.quantity}" min="1" style="width:60px;">
                                <button type="submit" class="btn btn-secondary">Update</button>
                            </form>
                        </td>
                        <td>
                            <fmt:formatNumber value="${item.product.basePrice * item.quantity}" type="currency" currencySymbol="Rs. "/>
                        </td>
                        <td>
                            <form method="post" action="${pageContext.request.contextPath}/cart">
                                <input type="hidden" name="action" value="remove">
                                <input type="hidden" name="cartItemId" value="${item.cartItemId}">
                                <button type="submit" class="btn btn-danger">Remove</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>

            <div class="cart-total">
                Total: <fmt:formatNumber value="${total}" type="currency" currencySymbol="Rs. "/>
            </div>

            <form method="post" action="${pageContext.request.contextPath}/cart">
                <input type="hidden" name="action" value="checkout">
                <button type="submit" class="btn btn-block">Checkout</button>
            </form>
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
