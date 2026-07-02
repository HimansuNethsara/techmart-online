package com.techmart.web.admin;

import com.techmart.dao.ProductDao;
import com.techmart.ejb.session.InventoryCacheService;
import com.techmart.entity.Product;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/admin/manage-products")
public class ManageProductsServlet extends HttpServlet {

    @EJB
    private ProductDao productDao;

    @EJB
    private InventoryCacheService inventoryService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("admin") == null) {
            resp.sendRedirect(req.getContextPath() + "/admin-login");
            return;
        }

        try {

            List<Product> products = productDao.findAllActive();

            Map<Integer, Integer> stockLevels = inventoryService.getCacheSnapshot();

            req.setAttribute("products", products);
            req.setAttribute("stockLevels", stockLevels);
            req.getRequestDispatcher("/manage-products.jsp").forward(req, resp);

        } catch (Exception e) {
            req.setAttribute("errorMessage", "Error loading products: " + e.getMessage());
            req.getRequestDispatcher("/manage-products.jsp").forward(req, resp);
        }
    }
}