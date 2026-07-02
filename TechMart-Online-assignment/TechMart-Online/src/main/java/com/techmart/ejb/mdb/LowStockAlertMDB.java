package com.techmart.ejb.mdb;

import com.techmart.dao.NotificationDao;
import com.techmart.entity.Notification;
import com.techmart.jms.LowStockAlertMessage;
import com.techmart.util.LifecycleLogger;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.EJB;
import jakarta.ejb.MessageDriven;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.ObjectMessage;

import java.util.logging.Level;
import java.util.logging.Logger;

@MessageDriven(
        activationConfig = {
                @ActivationConfigProperty(
                        propertyName = "destinationLookup",
                        propertyValue = "jms/LowStockTopic"
                ),
                @ActivationConfigProperty(
                        propertyName = "destinationType",
                        propertyValue = "jakarta.jms.Topic"
                ),
                @ActivationConfigProperty(
                        propertyName = "acknowledgeMode",
                        propertyValue = "Auto-acknowledge"
                )
        }
)
public class LowStockAlertMDB implements MessageListener {

    private static final Logger LOGGER = Logger.getLogger(LowStockAlertMDB.class.getName());

    @EJB
    private NotificationDao notificationDao;

    @PostConstruct
    public void init() {
        LifecycleLogger.log(LOGGER, LowStockAlertMDB.class,
                "PostConstruct (topic subscriber MDB instance created)", this);
    }

    @PreDestroy
    public void cleanup() {
        LifecycleLogger.log(LOGGER, LowStockAlertMDB.class,
                "PreDestroy (topic subscriber MDB instance removed)", this);
    }

    @Override
    public void onMessage(Message message) {
        try {
            if (!(message instanceof ObjectMessage)) {
                LOGGER.log(Level.WARNING, "Received non-ObjectMessage, ignoring: " + message);
                return;
            }

            ObjectMessage objectMessage = (ObjectMessage) message;
            Object body = objectMessage.getObject();

            if (!(body instanceof LowStockAlertMessage)) {
                LOGGER.log(Level.WARNING, "Unexpected payload type: " + body);
                return;
            }

            LowStockAlertMessage alert = (LowStockAlertMessage) body;
            LOGGER.log(Level.INFO, "LowStockAlertMDB received alert for productId="
                    + alert.getProductId() + " qty=" + alert.getCurrentQuantity());

            Notification saved = notificationDao.createSystemAlert(
                    Notification.TYPE_LOW_STOCK, alert.getMessage());
            LOGGER.log(Level.INFO, "Persisted LOW_STOCK notification, id="
                    + saved.getNotificationId());

        } catch (JMSException e) {
            LOGGER.log(Level.SEVERE, "Failed to process low-stock alert message.", e);
            throw new RuntimeException(e);
        }
    }
}