package com.techmart.web;

import com.techmart.dao.CustomerDao;
import com.techmart.dao.ProductDao;
import com.techmart.entity.Product;
import com.techmart.ejb.session.InventoryCacheService;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin-panel")
public class AdminPanelServlet extends HttpServlet {

    @EJB
    private ProductDao productDao;

    @EJB
    private CustomerDao customerDao;

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
            int totalProducts = products.size();
            int totalStock = 0;
            int lowStockCount = 0;

            for (Product product : products) {
                int stock = inventoryService.getAvailableQuantity(product.getProductId());
                totalStock += stock;
                if (inventoryService.isLowStock(product.getProductId())) {
                    lowStockCount++;
                }
            }

            long totalCustomers = customerDao.countAll();

            req.setAttribute("totalProducts", totalProducts);
            req.setAttribute("totalStock", totalStock);
            req.setAttribute("lowStockCount", lowStockCount);
            req.setAttribute("totalCustomers", totalCustomers);

        } catch (Exception e) {

            req.setAttribute("totalProducts", 0);
            req.setAttribute("totalStock", 0);
            req.setAttribute("lowStockCount", 0);
            req.setAttribute("totalCustomers", 0);
        }

        req.getRequestDispatcher("/admin.jsp").forward(req, resp);
    }
}