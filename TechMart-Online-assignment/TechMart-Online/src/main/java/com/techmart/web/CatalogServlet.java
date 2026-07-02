package com.techmart.web;

import com.techmart.ejb.session.InventoryCacheService;
import com.techmart.ejb.session.ProductCatalogService;
import com.techmart.entity.Product;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/catalog")
public class CatalogServlet extends HttpServlet {

    @EJB
    private ProductCatalogService catalogService;

    @EJB
    private InventoryCacheService inventoryCacheService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("customer") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String keyword = req.getParameter("keyword");
        String categoryIdParam = req.getParameter("categoryId");

        List<Product> products;
        if (keyword != null && !keyword.trim().isEmpty()) {
            products = catalogService.searchProducts(keyword.trim());
        } else if (categoryIdParam != null && !categoryIdParam.trim().isEmpty()) {
            products = catalogService.getProductsByCategory(Integer.parseInt(categoryIdParam));
        } else {
            products = catalogService.getAllActiveProducts();
        }

        Map<Integer, Integer> stockLevels = new HashMap<>();
        Map<Integer, Boolean> lowStockFlags = new HashMap<>();
        for (Product p : products) {
            stockLevels.put(p.getProductId(), inventoryCacheService.getAvailableQuantity(p.getProductId()));
            lowStockFlags.put(p.getProductId(), inventoryCacheService.isLowStock(p.getProductId()));
        }

        req.setAttribute("products", products);
        req.setAttribute("stockLevels", stockLevels);
        req.setAttribute("lowStockFlags", lowStockFlags);
        req.setAttribute("keyword", keyword);

        req.getRequestDispatcher("/catalog.jsp").forward(req, resp);
    }
}