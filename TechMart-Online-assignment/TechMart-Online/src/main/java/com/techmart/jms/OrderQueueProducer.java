package com.techmart.jms;

import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import jakarta.jms.*;

import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class OrderQueueProducer {

    private static final Logger LOGGER = Logger.getLogger(OrderQueueProducer.class.getName());

    @Resource(lookup = "jms/TechMartConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(lookup = "jms/OrderProcessingQueue")
    private Queue orderQueue;

    public void sendOrderPlaced(OrderPlacedMessage payload) {

        try (JMSContext context = connectionFactory.createContext()) {
            ObjectMessage message = context.createObjectMessage(payload);
            context.createProducer().send(orderQueue, message);

            LOGGER.log(Level.INFO, "Sent OrderPlacedMessage for orderId="
                    + payload.getOrderId() + " to jms/OrderProcessingQueue");

        } catch (JMSRuntimeException e) {

            LOGGER.log(Level.SEVERE, "Failed to send OrderPlacedMessage for orderId="
                    + payload.getOrderId(), e);
        }

    }

}