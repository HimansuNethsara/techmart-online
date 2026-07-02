package com.techmart.jms;

import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import jakarta.jms.JMSContext;
import jakarta.jms.JMSRuntimeException;
import jakarta.jms.ObjectMessage;
import jakarta.jms.Topic;
import jakarta.jms.ConnectionFactory;

import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class LowStockTopicProducer {

    private static final Logger LOGGER = Logger.getLogger(LowStockTopicProducer.class.getName());

    @Resource(lookup = "jms/TechMartConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(lookup = "jms/LowStockTopic")
    private Topic lowStockTopic;

    public void publishLowStockAlert(LowStockAlertMessage payload) {
        try (JMSContext context = connectionFactory.createContext()) {
            ObjectMessage message = context.createObjectMessage(payload);
            context.createProducer().send(lowStockTopic, message);

            LOGGER.log(Level.INFO, "Published LowStockAlertMessage for productId="
                    + payload.getProductId() + " to jms/LowStockTopic");

        } catch (JMSRuntimeException e) {
            LOGGER.log(Level.SEVERE, "Failed to publish LowStockAlertMessage for productId="
                    + payload.getProductId(), e);
        }
    }
}