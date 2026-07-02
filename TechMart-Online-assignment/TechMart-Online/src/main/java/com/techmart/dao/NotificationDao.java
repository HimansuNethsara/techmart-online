package com.techmart.dao;

import com.techmart.entity.Notification;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;

@Stateless
public class NotificationDao {

    @PersistenceContext(unitName = "techmartPU")
    private EntityManager em;

    // Create system alert (for everyone - LOW_STOCK, SYSTEM alerts)
    public Notification createSystemAlert(String type, String message) {
        Notification notification = new Notification();
        notification.setCustomer(null); // null means system-wide notification
        notification.setType(type);
        notification.setMessage(message);
        em.persist(notification);
        return notification;
    }

    // Create user-specific notification (for ORDER_CONFIRMATION, SHIPPING_UPDATE)
    public Notification createUserNotification(Integer customerId, String type, String message) {
        Notification notification = new Notification();
        // We'll fetch the customer reference
        com.techmart.entity.Customer customer = em.find(com.techmart.entity.Customer.class, customerId);
        notification.setCustomer(customer);
        notification.setType(type);
        notification.setMessage(message);
        em.persist(notification);
        return notification;
    }

    // Get recent notifications for everyone (system-wide)
    public List<Notification> findRecentSystemNotifications(int maxResults) {
        TypedQuery<Notification> query = em.createQuery(
                "SELECT n FROM Notification n WHERE n.customer IS NULL ORDER BY n.sentAt DESC",
                Notification.class
        );
        query.setMaxResults(maxResults);
        return query.getResultList();
    }

    // Get notifications for a specific customer (their own order confirmations + system-wide alerts)
    public List<Notification> findNotificationsForCustomer(Integer customerId, int maxResults) {
        TypedQuery<Notification> query = em.createQuery(
                "SELECT n FROM Notification n WHERE n.customer IS NULL OR n.customer.customerId = :customerId ORDER BY n.sentAt DESC",
                Notification.class
        );
        query.setParameter("customerId", customerId);
        query.setMaxResults(maxResults);
        return query.getResultList();
    }

    // Get all recent notifications (for admin panel - includes both system and user)
    public List<Notification> findRecentAll(int maxResults) {
        TypedQuery<Notification> query = em.createQuery(
                "SELECT n FROM Notification n ORDER BY n.sentAt DESC",
                Notification.class
        );
        query.setMaxResults(maxResults);
        return query.getResultList();
    }

    public List<Notification> findRecent(int maxResults) {
        return findRecentSystemNotifications(maxResults);
    }
}