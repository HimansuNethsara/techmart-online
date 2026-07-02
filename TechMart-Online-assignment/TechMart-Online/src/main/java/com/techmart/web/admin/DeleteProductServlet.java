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
import com.google.gson.JsonObject;

import java.io.IOException;

@WebServlet("/admin/delete-product")
public class DeleteProductServlet extends HttpServlet {

    @EJB
    private ProductDao productDao;

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

            if (productId == null) {
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Product ID is required");
                resp.getWriter().write(jsonResponse.toString());
                return;
            }

            Product product = productDao.findById(productId);
            if (product == null) {
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Product not found");
                resp.getWriter().write(jsonResponse.toString());
                return;
            }

            boolean newStatus = !product.getActive();
            product.setActive(newStatus);
            productDao.save(product);

            String statusMessage = newStatus ? "activated" : "deactivated";
            System.out.println("Product " + statusMessage + ": " + product.getName() + " (ID: " + productId + ")");

            inventoryService.refreshCache();

            jsonResponse.addProperty("success", true);
            jsonResponse.addProperty("message", "Product " + statusMessage + " successfully");
            jsonResponse.addProperty("active", newStatus);
            resp.getWriter().write(jsonResponse.toString());

        } catch (NumberFormatException e) {
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Invalid product ID");
            resp.getWriter().write(jsonResponse.toString());
        } catch (Exception e) {;
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Error updating product status: " + e.getMessage());
            resp.getWriter().write(jsonResponse.toString());
        }
    }
}