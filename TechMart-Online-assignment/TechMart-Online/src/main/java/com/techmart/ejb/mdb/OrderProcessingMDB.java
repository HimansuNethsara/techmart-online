package com.techmart.ejb.mdb;

import com.techmart.ejb.session.InventoryCacheService;
import com.techmart.jms.OrderPlacedMessage;
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
                        propertyValue = "jms/OrderProcessingQueue"
                ),
                @ActivationConfigProperty(
                        propertyName = "destinationType",
                        propertyValue = "jakarta.jms.Queue"
                ),
                @ActivationConfigProperty(
                        propertyName = "acknowledgeMode",
                        propertyValue = "Auto-acknowledge"
                )
        }
)
public class OrderProcessingMDB implements MessageListener {

    private static final Logger LOGGER = Logger.getLogger(OrderProcessingMDB.class.getName());

    @EJB
    private InventoryCacheService inventoryCacheService;

    @PostConstruct
    public void init() {
        LifecycleLogger.log(LOGGER, OrderProcessingMDB.class,
                "PostConstruct (MDB pool instance created, ready to consume)", this);
    }

    @PreDestroy
    public void cleanup() {
        LifecycleLogger.log(LOGGER, OrderProcessingMDB.class,
                "PreDestroy (MDB pool instance removed)", this);
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

            if (!(body instanceof OrderPlacedMessage)) {
                LOGGER.log(Level.WARNING, "Unexpected payload type: " + body);
                return;
            }

            OrderPlacedMessage orderPlaced = (OrderPlacedMessage) body;
            LOGGER.log(Level.INFO, "MDB received OrderPlacedMessage for orderId="
                    + orderPlaced.getOrderId() + " (" + orderPlaced.getItems().size() + " line items)");

            for (OrderPlacedMessage.LineItem item : orderPlaced.getItems()) {
                inventoryCacheService.adjustQuantity(item.getProductId(), -item.getQuantity());
                LOGGER.log(Level.FINE, "Deducted qty=" + item.getQuantity()
                        + " for productId=" + item.getProductId()
                        + " (orderId=" + orderPlaced.getOrderId() + ")");
            }

            LOGGER.log(Level.INFO, "Inventory updated for orderId=" + orderPlaced.getOrderId());

        } catch (JMSException e) {
            LOGGER.log(Level.SEVERE, "Failed to process order message, will be redelivered.", e);
            throw new RuntimeException(e);
        }
    }
}