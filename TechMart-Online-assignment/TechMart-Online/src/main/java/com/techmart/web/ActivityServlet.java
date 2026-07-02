package com.techmart.web;

import com.techmart.dao.NotificationDao;
import com.techmart.ejb.session.InventoryCacheService;
import com.techmart.entity.Notification;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@WebServlet("/activity")
public class ActivityServlet extends HttpServlet {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    @EJB
    private NotificationDao notificationDao;

    @EJB
    private InventoryCacheService inventoryCacheService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        HttpSession session = req.getSession(false);
        Integer customerId = null;
        if (session != null) {
            com.techmart.entity.Customer customer = (com.techmart.entity.Customer) session.getAttribute("customer");
            if (customer != null) {
                customerId = customer.getCustomerId();
            }
        }

        List<Notification> recent;
        if (customerId != null) {
            // For logged-in customers: show system alerts + their own order confirmations
            recent = notificationDao.findNotificationsForCustomer(customerId, 20);
        } else {
            recent = notificationDao.findRecentSystemNotifications(20);
        }

        Map<Integer, Integer> snapshot = inventoryCacheService.getCacheSnapshot();

        StringBuilder json = new StringBuilder();
        json.append("{\"notifications\":[");
        for (int i = 0; i < recent.size(); i++) {
            Notification n = recent.get(i);
            if (i > 0) json.append(",");

            // Determine notification type label
            String typeLabel = n.getType();
            if (n.getCustomer() != null && Notification.TYPE_ORDER_CONFIRMATION.equals(n.getType())) {
                typeLabel = "Your Order";
            }

            json.append("{")
                    .append("\"type\":\"").append(escape(typeLabel)).append("\",")
                    .append("\"message\":\"").append(escape(n.getMessage())).append("\",")
                    .append("\"time\":\"").append(n.getSentAt().format(TIME_FORMAT)).append("\"")
                    .append("}");
        }
        json.append("],\"inventorySnapshot\":{");
        int i = 0;
        for (Map.Entry<Integer, Integer> entry : snapshot.entrySet()) {
            if (i++ > 0) json.append(",");
            json.append("\"").append(entry.getKey()).append("\":").append(entry.getValue());
        }
        json.append("}}");

        out.print(json);
    }

    private String escape(String s) {
        return s == null ? "" : s.replace("\"", "\\\"");
    }
}