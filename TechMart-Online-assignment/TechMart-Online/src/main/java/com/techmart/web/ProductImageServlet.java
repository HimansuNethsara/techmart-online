package com.techmart.web;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@WebServlet("/images/products/*")
public class ProductImageServlet extends HttpServlet {

    private static final String IMAGE_FOLDER =
            "J:/java intellij practicals/BCD/BCD I Practicals/TechMart-Online-assignment/images/products";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String filename = req.getPathInfo();

        if (filename == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        File file = new File(IMAGE_FOLDER, filename.substring(1));

        if (!file.exists()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String mime = getServletContext().getMimeType(file.getName());

        if (mime == null) {
            mime = "application/octet-stream";
        }

        resp.setContentType(mime);

        Files.copy(file.toPath(), resp.getOutputStream());
    }
}
