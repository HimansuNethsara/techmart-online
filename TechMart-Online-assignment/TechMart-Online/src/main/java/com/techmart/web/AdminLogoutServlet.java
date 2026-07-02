package com.techmart.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/admin-logout")
public class AdminLogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session != null) {

            String adminEmail = (String) session.getAttribute("adminEmail");
            if (adminEmail != null) {
                System.out.println("Admin logged out: " + adminEmail);
            }

            session.invalidate();
        }

        resp.sendRedirect(req.getContextPath() + "/admin-login?logout=success");
    }
}