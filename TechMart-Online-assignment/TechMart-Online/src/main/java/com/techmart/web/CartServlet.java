package com.techmart.web;

import com.techmart.ejb.session.ShoppingCartService;
import jakarta.ejb.EJBException;
import jakarta.ejb.NoSuchEJBException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/cart")
public class CartServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        ShoppingCartService cart = requireCartOrRedirect(req, resp);
        if (cart == null) return;

        boolean justCheckedOut = req.getParameter("checkoutOrderId") != null;

        if (justCheckedOut) {
            req.setAttribute("items", java.util.Collections.emptyList());
            req.setAttribute("total", java.math.BigDecimal.ZERO);
            req.setAttribute("checkoutOrderId", req.getParameter("checkoutOrderId"));

            req.getRequestDispatcher("/cart.jsp").forward(req, resp);
            return;
        }

        try {
            req.setAttribute("items", cart.getItems());
            req.setAttribute("total", cart.getCartTotal());
        } catch (IllegalStateException e) {
            if (justCheckedOut) {
                req.setAttribute("items", java.util.Collections.emptyList());
                req.setAttribute("total", java.math.BigDecimal.ZERO);
            } else {
                recoverFromDeadCartBean(req, resp);
                return;
            }
        } catch (EJBException e) {
            if (isDeadEjbReference(e)) {
                recoverFromDeadCartBean(req, resp);
                return;
            }
            throw e;
        }
        req.setAttribute("checkoutOrderId", req.getParameter("checkoutOrderId"));
        req.getRequestDispatcher("/cart.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        ShoppingCartService cart = requireCartOrRedirect(req, resp);
        if (cart == null) return;

        String action = req.getParameter("action");

        try {
            switch (action) {
                case "add": {
                    Integer productId = Integer.parseInt(req.getParameter("productId"));
                    int qty = Integer.parseInt(req.getParameter("quantity"));
                    cart.addItem(productId, qty);
                    break;
                }
                case "update": {
                    Integer cartItemId = Integer.parseInt(req.getParameter("cartItemId"));
                    int qty = Integer.parseInt(req.getParameter("quantity"));
                    cart.updateQuantity(cartItemId, qty);
                    break;
                }
                case "remove": {
                    Integer cartItemId = Integer.parseInt(req.getParameter("cartItemId"));
                    cart.removeItem(cartItemId);
                    break;
                }
                case "checkout": {
                    Integer orderId = cart.checkout();
                    if (orderId == null) {
                        req.setAttribute("errorMessage",
                                "Checkout completed but no order ID was returned. Please check your orders.");
                        resp.sendRedirect(req.getContextPath() + "/cart");
                        return;
                    }
                    resp.sendRedirect(req.getContextPath() + "/cart?checkoutOrderId=" + orderId);
                    return;
                }
                default:
                    req.setAttribute("errorMessage", "Unknown cart action.");
            }
        } catch (IllegalStateException | IllegalArgumentException e) {
            req.setAttribute("errorMessage", e.getMessage());
        } catch (EJBException e) {
            if (isDeadEjbReference(e)) {
                recoverFromDeadCartBean(req, resp);
                return;
            }
            throw e;
        }

        resp.sendRedirect(req.getContextPath() + "/cart");
    }

    private boolean isDeadEjbReference(EJBException e) {
        return (e instanceof NoSuchEJBException)
                || (e.getCause() instanceof NoSuchEJBException);
    }

    private void recoverFromDeadCartBean(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        resp.sendRedirect(req.getContextPath() + "/login?sessionExpired=true");
    }

    private ShoppingCartService requireCartOrRedirect(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("cartService") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return null;
        }
        return (ShoppingCartService) session.getAttribute("cartService");
    }
}