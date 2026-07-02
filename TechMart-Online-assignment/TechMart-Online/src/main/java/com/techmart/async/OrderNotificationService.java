package com.techmart.async;

import java.util.concurrent.Future;

public interface OrderNotificationService {

    Future<Boolean> sendOrderConfirmation(Integer orderId, Integer customerId, String customerEmail);

}