package com.techmart.web;

import com.techmart.dao.CustomerDao;
import com.techmart.entity.Customer;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;

@WebServlet("/signup")
public class SignupServlet extends HttpServlet {

    @EJB
    private CustomerDao customerDao;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/signup.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String fullName = req.getParameter("fullName");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");

        if (fullName == null || fullName.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                confirmPassword == null || confirmPassword.trim().isEmpty()) {

            req.setAttribute("errorMessage", "All fields are required.");
            req.getRequestDispatcher("/signup.jsp").forward(req, resp);
            return;
        }

        if (password.length() < 8) {
            req.setAttribute("errorMessage", "Password must be at least 8 characters long.");
            req.getRequestDispatcher("/signup.jsp").forward(req, resp);
            return;
        }

        if (!password.equals(confirmPassword)) {
            req.setAttribute("errorMessage", "Passwords do not match.");
            req.getRequestDispatcher("/signup.jsp").forward(req, resp);
            return;
        }

        Customer existingCustomer = customerDao.findByEmail(email);
        if (existingCustomer != null) {
            req.setAttribute("errorMessage", "Email address is already registered. Please use a different email or login.");
            req.getRequestDispatcher("/signup.jsp").forward(req, resp);
            return;
        }

        String[] nameParts = fullName.trim().split("\\s+", 2);
        String firstName = nameParts[0];
        String lastName = nameParts.length > 1 ? nameParts[1] : "";

        Customer newCustomer = new Customer();
        newCustomer.setFirstName(firstName);
        newCustomer.setLastName(lastName);
        newCustomer.setEmail(email);
        newCustomer.setPasswordHash(password);
        newCustomer.setCreatedAt(LocalDateTime.now());

        try {

            customerDao.save(newCustomer);

            req.setAttribute("successMessage", "Account created successfully! Please login.");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);

        } catch (Exception e) {
            req.setAttribute("errorMessage", "An error occurred during registration. Please try again.");
            req.getRequestDispatcher("/signup.jsp").forward(req, resp);
        }
    }
}