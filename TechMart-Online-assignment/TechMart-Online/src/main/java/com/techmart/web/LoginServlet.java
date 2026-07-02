package com.techmart.web;

import com.techmart.dao.CustomerDao;
import com.techmart.ejb.session.ShoppingCartService;
import com.techmart.entity.Customer;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @EJB
    private CustomerDao customerDao;

    @EJB
    private ShoppingCartService cartService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String email = req.getParameter("email");
        String password = req.getParameter("password");

        Customer customer = customerDao.findByEmail(email);

        if (customer == null || !customer.getPasswordHash().equals(password)) {
            req.setAttribute("errorMessage", "Invalid email or password.");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
            return;
        }

        HttpSession session = req.getSession(true);
        session.setAttribute("customer", customer);

        cartService.initCart(customer.getCustomerId());
        session.setAttribute("cartService", cartService);

        resp.sendRedirect(req.getContextPath() + "/catalog");
    }
}