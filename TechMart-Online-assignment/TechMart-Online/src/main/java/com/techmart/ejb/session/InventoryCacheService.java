package com.techmart.ejb.session;

import java.util.Map;

public interface InventoryCacheService {

    int getAvailableQuantity(Integer productId);

    boolean isLowStock(Integer productId);

    int adjustQuantity(Integer productId, int delta);

    void refreshCache();

    Map<Integer, Integer> getCacheSnapshot();
}