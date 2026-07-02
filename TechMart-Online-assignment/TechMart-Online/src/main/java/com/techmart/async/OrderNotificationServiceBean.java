package com.techmart.async;

import com.techmart.dao.NotificationDao;
import com.techmart.entity.Notification;
import com.techmart.util.LifecycleLogger;
import jakarta.ejb.AsyncResult;
import jakarta.ejb.Asynchronous;
import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Stateless;

import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
@Local(OrderNotificationService.class)
public class OrderNotificationServiceBean implements OrderNotificationService {

    private static final Logger LOGGER = Logger.getLogger(OrderNotificationServiceBean.class.getName());

    @EJB
    private NotificationDao notificationDao;

    @Override
    @Asynchronous
    public Future<Boolean> sendOrderConfirmation(Integer orderId, Integer customerId, String customerEmail) {
        LifecycleLogger.log(LOGGER, OrderNotificationServiceBean.class,
                "Asynchronous method started on container thread "
                        + Thread.currentThread().getName(), this);

        try {

            Thread.sleep(3000);

            if (customerEmail == null || !customerEmail.contains("@")) {
                throw new IllegalArgumentException("Invalid customer email: " + customerEmail);
            }

            // Create user-specific notification
            String message = "Your TechMart order #" + orderId + " has been confirmed.";
            Notification saved = notificationDao.createUserNotification(
                    customerId,
                    Notification.TYPE_ORDER_CONFIRMATION,
                    message
            );

            LOGGER.log(Level.INFO, "Order confirmation sent for orderId=" + orderId
                    + " customerId=" + customerId
                    + " (notificationId=" + saved.getNotificationId() + ")");

            return new AsyncResult<>(true);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.log(Level.SEVERE, "Notification send interrupted for orderId=" + orderId, e);
            return new AsyncResult<>(false);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to send order confirmation for orderId=" + orderId, e);
            return new AsyncResult<>(false);
        }
    }
}