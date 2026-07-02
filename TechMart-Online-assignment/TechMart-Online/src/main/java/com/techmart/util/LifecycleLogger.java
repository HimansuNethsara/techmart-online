package com.techmart.util;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class LifecycleLogger {

    private static final DateTimeFormatter FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private LifecycleLogger() {

    }

    public static void log(Logger logger, Class<?> beanClass, String event, Object beanInstance) {
        String timestamp = LocalDateTime.now().format(FORMAT);
        int instanceId = System.identityHashCode(beanInstance);
        logger.log(Level.INFO, String.format(
                "[LIFECYCLE] %s | bean=%s | event=%s | instanceId=%d",
                timestamp, beanClass.getSimpleName(), event, instanceId
        ));
    }

}