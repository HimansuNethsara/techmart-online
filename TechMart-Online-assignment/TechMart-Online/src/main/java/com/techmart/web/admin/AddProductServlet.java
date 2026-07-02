package com.techmart.web.admin;

import com.techmart.dao.CategoryDao;
import com.techmart.dao.InventoryDao;
import com.techmart.dao.ProductDao;
import com.techmart.dao.WarehouseDao;
import com.techmart.ejb.session.InventoryCacheService;
import com.techmart.entity.Category;
import com.techmart.entity.Product;
import com.techmart.entity.Warehouse;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/admin/add-product")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2,
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 50
)
public class AddProductServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(AddProductServlet.class.getName());

    @EJB
    private ProductDao productDao;

    @EJB
    private CategoryDao categoryDao;

    @EJB
    private WarehouseDao warehouseDao;

    @EJB
    private InventoryDao inventoryDao;

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

        List<Category> categories = categoryDao.findAll();
        List<Warehouse> warehouses = warehouseDao.findAll();

        req.setAttribute("categories", categories);
        req.setAttribute("warehouses", warehouses);

        req.getRequestDispatcher("/add-product.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("admin") == null) {
            resp.sendRedirect(req.getContextPath() + "/admin-login");
            return;
        }

        try {

            Integer categoryId = Integer.parseInt(req.getParameter("categoryId"));
            String name = req.getParameter("name");
            String brand = req.getParameter("brand");
            String description = req.getParameter("description");
            BigDecimal basePrice = new BigDecimal(req.getParameter("basePrice"));

            Integer warehouseId = Integer.parseInt(req.getParameter("warehouseId"));
            Integer stockQuantity = Integer.parseInt(req.getParameter("stockQuantity"));
            Integer reorderThreshold = Integer.parseInt(req.getParameter("reorderThreshold"));

            Part filePart = req.getPart("imageFile");
            String imagePath = null;

            if (filePart != null && filePart.getSize() > 0) {

                String originalFileName = Paths.get(filePart.getSubmittedFileName())
                        .getFileName()
                        .toString();

                String extension = "";

                int dot = originalFileName.lastIndexOf(".");
                if (dot > -1) {
                    extension = originalFileName.substring(dot);
                }

                String uniqueFileName = System.currentTimeMillis() + extension;

                String uploadFolderPath = "J:/java intellij practicals/BCD/practicals/TechMart-Online-assignment/images/products";
                File uploadFolder = new File(uploadFolderPath);

                if (!uploadFolder.exists()) {
                    uploadFolder.mkdirs();
                }

                File destination = new File(uploadFolder, uniqueFileName);

                try (var input = filePart.getInputStream()) {
                    Files.copy(
                            input,
                            destination.toPath(),
                            StandardCopyOption.REPLACE_EXISTING
                    );
                }

                imagePath = "images/products/" + uniqueFileName;

                LOGGER.log(Level.INFO,
                        "Image upload -> contextPath=" + req.getContextPath()
                                + " | uploadFolder=" + uploadFolder.getAbsolutePath()
                                + " | savedFile=" + destination.getAbsolutePath()
                                + " | dbPath=" + imagePath);
            }

            Category category = categoryDao.findById(categoryId);

            if (category == null) {
                req.setAttribute("errorMessage", "Invalid category selected.");
                doGet(req, resp);
                return;
            }

            Warehouse warehouse = warehouseDao.findById(warehouseId);

            if (warehouse == null) {
                req.setAttribute("errorMessage", "Invalid warehouse selected.");
                doGet(req, resp);
                return;
            }

            Product product = new Product();

            product.setCategory(category);
            product.setName(name);
            product.setBrand(brand);
            product.setDescription(description);
            product.setBasePrice(basePrice);
            product.setImagePath(imagePath);
            product.setActive(true);

            productDao.save(product);


            inventoryDao.createInventory(product, warehouse, stockQuantity, reorderThreshold);

            inventoryService.refreshCache();

            req.setAttribute(
                    "successMessage",
                    "Product '" + name + "' added successfully with "
                            + stockQuantity
                            + " units in "
                            + warehouse.getName()
                            + "!"
            );

            List<Category> categories = categoryDao.findAll();
            List<Warehouse> warehouses = warehouseDao.findAll();

            req.setAttribute("categories", categories);
            req.setAttribute("warehouses", warehouses);

            req.getRequestDispatcher("/add-product.jsp").forward(req, resp);

        } catch (NumberFormatException e) {

            req.setAttribute(
                    "errorMessage",
                    "Invalid number format. Please check your inputs."
            );

            doGet(req, resp);

        } catch (Exception e) {

            LOGGER.log(Level.SEVERE, "Error adding product", e);

            req.setAttribute(
                    "errorMessage",
                    "Error adding product: " + e.getMessage()
            );

            doGet(req, resp);
        }
    }
}