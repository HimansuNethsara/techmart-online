package com.techmart.web;

import com.techmart.dao.OrderDao;
import com.techmart.entity.CustomerOrder;
import com.techmart.entity.Customer;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/orders")
public class OrderServlet extends HttpServlet {

    @EJB
    private OrderDao orderDao;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("customer") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Customer customer = (Customer) session.getAttribute("customer");

        try {
            // Get all orders for the customer
            List<CustomerOrder> orders = orderDao.findByCustomerId(customer.getCustomerId());
            req.setAttribute("orders", orders);
            req.getRequestDispatcher("/orders.jsp").forward(req, resp);

        } catch (Exception e) {
            req.setAttribute("errorMessage", "Error loading orders: " + e.getMessage());
            req.getRequestDispatcher("/orders.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("customer") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String action = req.getParameter("action");
        String orderIdParam = req.getParameter("orderId");

        if (orderIdParam == null || orderIdParam.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/orders");
            return;
        }

        try {
            Integer orderId = Integer.parseInt(orderIdParam);

            if ("cancel".equals(action)) {
                CustomerOrder order = orderDao.findById(orderId);
                if (order != null && CustomerOrder.STATUS_PENDING.equals(order.getStatus())) {
                    orderDao.updateStatus(orderId, CustomerOrder.STATUS_CANCELLED);
                }
            }

            resp.sendRedirect(req.getContextPath() + "/orders");

        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/orders");
        }
    }
}