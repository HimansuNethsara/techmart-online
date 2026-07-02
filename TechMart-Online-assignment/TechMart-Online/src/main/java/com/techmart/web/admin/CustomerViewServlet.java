package com.techmart.web.admin;

import com.techmart.dao.CustomerDao;
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

@WebServlet("/admin/customers")
public class CustomerViewServlet extends HttpServlet {

    @EJB
    private CustomerDao customerDao;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("admin") == null) {
            resp.sendRedirect(req.getContextPath() + "/admin-login");
            return;
        }

        try {

            String search = req.getParameter("search");
            List<Customer> customers;

            if (search != null && !search.trim().isEmpty()) {
                customers = customerDao.search(search.trim());
            } else {
                customers = customerDao.findAll();
            }

            long totalCustomers = customerDao.countAll();

            req.setAttribute("customers", customers);
            req.setAttribute("totalCustomers", totalCustomers);
            req.setAttribute("searchTerm", search);
            req.getRequestDispatcher("/admin-customers.jsp").forward(req, resp);

        } catch (Exception e) {
            req.setAttribute("errorMessage", "Error loading customers: " + e.getMessage());
            req.getRequestDispatcher("/admin-customers.jsp").forward(req, resp);
        }
    }
}