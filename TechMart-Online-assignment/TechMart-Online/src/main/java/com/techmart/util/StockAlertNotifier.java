package com.techmart.util;

import com.techmart.ejb.session.InventoryCacheService;
import com.techmart.jms.LowStockAlertMessage;
import com.techmart.jms.LowStockTopicProducer;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class StockAlertNotifier {

    private static final Logger LOGGER = Logger.getLogger(StockAlertNotifier.class.getName());

    private static final String INVENTORY_CACHE_JNDI_NAME =
            "java:global/techmart-online/InventoryCacheServiceBean!com.techmart.ejb.session.InventoryCacheService";

    private static final String TOPIC_PRODUCER_JNDI_NAME =
            "java:global/techmart-online/LowStockTopicProducer";

    private StockAlertNotifier() {
    }

    public static String checkAndNotify(Integer productId) {
        try {
            InitialContext ctx = new InitialContext();

            long lookupStart = System.nanoTime();
            InventoryCacheService inventoryCache =
                    (InventoryCacheService) ctx.lookup(INVENTORY_CACHE_JNDI_NAME);
            long lookupMicros = (System.nanoTime() - lookupStart) / 1000;
            LOGGER.log(Level.FINE, "Manual JNDI lookup of InventoryCacheService took "
                    + lookupMicros + " microseconds.");

            if (!inventoryCache.isLowStock(productId)) {
                return null;
            }

            int qty = inventoryCache.getAvailableQuantity(productId);
            String message = "LOW STOCK ALERT: productId=" + productId
                    + " has only " + qty + " units remaining.";

            long lookupStart2 = System.nanoTime();
            LowStockTopicProducer topicProducer =
                    (LowStockTopicProducer) ctx.lookup(TOPIC_PRODUCER_JNDI_NAME);
            long lookupMicros2 = (System.nanoTime() - lookupStart2) / 1000;
            LOGGER.log(Level.FINE, "Manual JNDI lookup of LowStockTopicProducer took "
                    + lookupMicros2 + " microseconds.");

            topicProducer.publishLowStockAlert(
                    new LowStockAlertMessage(productId, qty, message));

            return message;

        } catch (NamingException e) {
            LOGGER.log(Level.FINE,
                    "JNDI lookup unavailable (expected when running outside the EJB container).", e);
            return null;
        }
    }
}