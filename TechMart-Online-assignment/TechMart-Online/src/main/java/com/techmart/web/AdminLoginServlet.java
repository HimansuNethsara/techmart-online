package com.techmart.web;

import com.techmart.dao.AdminDao;
import com.techmart.entity.Admin;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/admin-login")
public class AdminLoginServlet extends HttpServlet {

    @EJB
    private AdminDao adminDao;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("admin") != null) {

            resp.sendRedirect(req.getContextPath() + "/admin-panel");
            return;
        }
        req.getRequestDispatcher("/admin-login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String email = req.getParameter("email");
        String password = req.getParameter("password");

        if (email == null || email.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {

            req.setAttribute("errorMessage", "Email and password are required.");
            req.getRequestDispatcher("/admin-login.jsp").forward(req, resp);
            return;
        }

        Admin admin = adminDao.findByEmail(email.trim());

        if (admin == null || !admin.getPassword().equals(password)) {
            req.setAttribute("errorMessage", "Invalid admin credentials. Please try again.");
            req.getRequestDispatcher("/admin-login.jsp").forward(req, resp);
            return;
        }

        HttpSession session = req.getSession(true);
        session.setAttribute("admin", admin);
        session.setAttribute("adminEmail", admin.getEmail());
        session.setAttribute("adminId", admin.getAdminId());

        System.out.println("Admin logged in: " + admin.getEmail() + " (ID: " + admin.getAdminId() + ")");

        resp.sendRedirect(req.getContextPath() + "/admin-panel");
    }
}