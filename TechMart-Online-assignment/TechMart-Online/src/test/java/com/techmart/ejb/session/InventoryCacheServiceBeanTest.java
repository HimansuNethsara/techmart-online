package com.techmart.ejb.session;

import com.techmart.dao.InventoryDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;

import java.lang.reflect.Method;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryCacheServiceBeanTest {

    @Mock
    private InventoryDao inventoryDao;

    @InjectMocks
    private InventoryCacheServiceBean inventoryCache;

    @BeforeEach
    void setUp() {

    }

    @Test
    void getAvailableQuantity_returnsZero_forUnknownProduct() {
        assertEquals(0, inventoryCache.getAvailableQuantity(999));
    }

    @Test
    void isLowStock_isFalse_whenQuantityIsAboveThreshold() {
        inventoryCache.adjustQuantity(1, 10); // 10 > threshold of 5
        assertFalse(inventoryCache.isLowStock(1));
    }

    @Test
    void isLowStock_isTrue_whenQuantityEqualsThreshold() {
        inventoryCache.adjustQuantity(2, 5);
        assertTrue(inventoryCache.isLowStock(2));
    }

    @Test
    void isLowStock_isTrue_whenQuantityIsBelowThreshold() {
        inventoryCache.adjustQuantity(3, 2);
        assertTrue(inventoryCache.isLowStock(3));
    }

    @Test
    void adjustQuantity_accumulatesAcrossMultipleCalls() {
        inventoryCache.adjustQuantity(4, 10);
        inventoryCache.adjustQuantity(4, -3); // simulate a sale of 3 units

        assertEquals(7, inventoryCache.getAvailableQuantity(4));
    }

    @Test
    void adjustQuantity_alsoUpdatesUnderlyingDatabase() {
        inventoryCache.adjustQuantity(5, -2);

        verify(inventoryDao).adjustFirstWarehouseStock(5, -2);
    }

    @Test
    void getCacheSnapshot_reflectsAllSeededProducts() {
        inventoryCache.adjustQuantity(10, 50);
        inventoryCache.adjustQuantity(20, 8);

        Map<Integer, Integer> snapshot = inventoryCache.getCacheSnapshot();

        Map<Integer, Integer> expected = new HashMap<>();
        expected.put(10, 50);
        expected.put(20, 8);
        assertEquals(expected, snapshot);
    }

    @Test
    void getCacheSnapshot_isUnmodifiable() {
        inventoryCache.adjustQuantity(1, 5);
        Map<Integer, Integer> snapshot = inventoryCache.getCacheSnapshot();

        assertThrows(UnsupportedOperationException.class,
                () -> snapshot.put(999, 999));
    }
}