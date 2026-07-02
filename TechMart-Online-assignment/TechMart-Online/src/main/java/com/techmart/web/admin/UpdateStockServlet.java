package com.techmart.web.admin;

import com.techmart.ejb.session.InventoryCacheService;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.google.gson.JsonObject;

import java.io.IOException;

@WebServlet("/admin/update-stock")
public class UpdateStockServlet extends HttpServlet {

    @EJB
    private InventoryCacheService inventoryService;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("admin") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        JsonObject jsonResponse = new JsonObject();
        resp.setContentType("application/json");

        try {
            Integer productId = Integer.parseInt(req.getParameter("productId"));
            Integer newStock = Integer.parseInt(req.getParameter("stockQuantity"));

            if (productId == null || newStock == null) {
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Product ID and stock quantity are required");
                resp.getWriter().write(jsonResponse.toString());
                return;
            }

            if (newStock < 0) {
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Stock quantity cannot be negative");
                resp.getWriter().write(jsonResponse.toString());
                return;
            }

            int currentStock = inventoryService.getAvailableQuantity(productId);
            int delta = newStock - currentStock;

            if (delta != 0) {
                inventoryService.adjustQuantity(productId, delta);
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("message", "Stock updated successfully");
                jsonResponse.addProperty("newStock", newStock);
            } else {
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("message", "Stock is already at this level");
                jsonResponse.addProperty("newStock", newStock);
            }

            resp.getWriter().write(jsonResponse.toString());

        } catch (NumberFormatException e) {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Invalid number format");
            resp.getWriter().write(jsonResponse.toString());
        } catch (Exception e) {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Error updating stock: " + e.getMessage());
            resp.getWriter().write(jsonResponse.toString());
        }
    }
}