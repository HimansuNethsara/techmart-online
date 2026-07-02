package com.techmart.ejb.session;

import com.techmart.dao.InventoryDao;
import com.techmart.util.LifecycleLogger;
import com.techmart.util.StockAlertNotifier;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
@Startup
@Local(InventoryCacheService.class)
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class InventoryCacheServiceBean implements InventoryCacheService {

    private static final Logger LOGGER = Logger.getLogger(InventoryCacheServiceBean.class.getName());
    private static final int DEFAULT_LOW_STOCK_THRESHOLD = 5;

    @EJB
    private InventoryDao inventoryDao;

    private Map<Integer, Integer> productIdToQuantity = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        LifecycleLogger.log(LOGGER, InventoryCacheServiceBean.class, "PostConstruct (@Startup singleton created - loading initial cache from DB)", this);
        loadFromDatabase();
        LOGGER.log(Level.INFO, "Inventory cache loaded: {0} products.", productIdToQuantity.size());
    }

    @PreDestroy
    public void cleanup() {
        LifecycleLogger.log(LOGGER, InventoryCacheServiceBean.class, "PreDestroy (@Startup singleton shutting down - releasing cache)", this);
        productIdToQuantity.clear();
    }

    @Override
    @Lock(LockType.READ)
    public int getAvailableQuantity(Integer productId) {
        return productIdToQuantity.getOrDefault(productId, 0);
    }

    @Override
    @Lock(LockType.READ)
    public boolean isLowStock(Integer productId) {
        return getAvailableQuantity(productId) <= DEFAULT_LOW_STOCK_THRESHOLD;
    }

    @Override
    @Lock(LockType.WRITE)
    public int adjustQuantity(Integer productId, int delta) {
        int updated = productIdToQuantity.merge(productId, delta, Integer::sum);
        inventoryDao.adjustFirstWarehouseStock(productId, delta);
        LOGGER.log(Level.FINE, "Adjusted productId=" + productId + " by " + delta + " -> new total=" + updated);

        String alert = StockAlertNotifier.checkAndNotify(productId);
        if (alert != null) {
            LOGGER.log(Level.WARNING, alert);
        }

        return updated;
    }

    @Override
    @Lock(LockType.WRITE)
    public void refreshCache() {
        loadFromDatabase();
    }

    @Override
    @Lock(LockType.READ)
    public Map<Integer, Integer> getCacheSnapshot() {
        return Collections.unmodifiableMap(new HashMap<>(productIdToQuantity));
    }

    private void loadFromDatabase() {
        Map<Integer, Integer> fresh = inventoryDao.loadAllProductTotals();
        productIdToQuantity = new ConcurrentHashMap<>(fresh);
    }
}