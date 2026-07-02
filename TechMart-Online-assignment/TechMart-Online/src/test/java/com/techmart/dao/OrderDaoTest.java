package com.techmart.dao;

import com.techmart.entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderDaoTest {

    @Mock
    private EntityManager em;

    @Mock
    private TypedQuery<CustomerOrder> typedQuery;

    @InjectMocks
    private OrderDao orderDao;

    private Cart buildCartWithCustomer() {
        Customer customer = new Customer();
        customer.setCustomerId(1);

        Cart cart = new Cart();
        cart.setCartId(10);
        cart.setCustomer(customer);
        cart.setStatus(Cart.STATUS_ACTIVE);
        return cart;
    }

    private CartItem buildItem(BigDecimal unitPrice, int quantity) {
        Product product = new Product();
        product.setBasePrice(unitPrice);

        CartItem item = new CartItem();
        item.setProduct(product);
        item.setQuantity(quantity);
        return item;
    }

    @Test
    void placeOrder_calculatesCorrectTotal_forSingleItem() {
        Cart cart = buildCartWithCustomer();
        List<CartItem> items = new ArrayList<>();
        items.add(buildItem(new BigDecimal("25.50"), 3)); // 76.50

        CustomerOrder order = orderDao.placeOrder(cart, items);

        assertEquals(new BigDecimal("76.50"), order.getTotalAmount());
    }

    @Test
    void placeOrder_calculatesCorrectTotal_acrossMultipleItems() {
        Cart cart = buildCartWithCustomer();
        List<CartItem> items = new ArrayList<>();
        items.add(buildItem(new BigDecimal("10.00"), 2)); // 20.00
        items.add(buildItem(new BigDecimal("5.25"), 4));  // 21.00

        CustomerOrder order = orderDao.placeOrder(cart, items);

        assertEquals(new BigDecimal("41.00"), order.getTotalAmount());
    }

    @Test
    void placeOrder_setsStatusPending_onNewOrder() {
        Cart cart = buildCartWithCustomer();
        List<CartItem> items = new ArrayList<>();
        items.add(buildItem(new BigDecimal("10.00"), 1));

        CustomerOrder order = orderDao.placeOrder(cart, items);

        assertEquals(CustomerOrder.STATUS_PENDING, order.getStatus());
    }

    @Test
    void placeOrder_marksCartAsCheckedOut() {
        Cart cart = buildCartWithCustomer();
        List<CartItem> items = new ArrayList<>();
        items.add(buildItem(new BigDecimal("10.00"), 1));

        orderDao.placeOrder(cart, items);

        assertEquals(Cart.STATUS_CHECKED_OUT, cart.getStatus());
    }

    @Test
    void placeOrder_snapshotsUnitPriceOntoOrderItem_independentOfLaterProductChanges() {
        Cart cart = buildCartWithCustomer();
        BigDecimal priceAtPurchaseTime = new BigDecimal("99.99");
        CartItem item = buildItem(priceAtPurchaseTime, 1);
        List<CartItem> items = new ArrayList<>();
        items.add(item);

        CustomerOrder order = orderDao.placeOrder(cart, items);

        item.getProduct().setBasePrice(new BigDecimal("199.99"));

        OrderItem savedLineItem = order.getItems().get(0);
        assertEquals(priceAtPurchaseTime, savedLineItem.getUnitPrice(),
                "OrderItem must keep the price snapshot taken at purchase time, " +
                        "unaffected by later changes to the product's current price.");
    }

    @Test
    void placeOrder_callsFlushImmediatelyAfterPersist() {
        Cart cart = buildCartWithCustomer();
        List<CartItem> items = new ArrayList<>();
        items.add(buildItem(new BigDecimal("10.00"), 1));

        orderDao.placeOrder(cart, items);

        var inOrder = inOrder(em);
        inOrder.verify(em).persist(any(CustomerOrder.class));
        inOrder.verify(em).flush();
    }


    @Test
    void findByCustomerId_returnsOrdersForCustomer() {
        Integer customerId = 1;
        List<CustomerOrder> expectedOrders = new ArrayList<>();
        expectedOrders.add(new CustomerOrder());
        expectedOrders.add(new CustomerOrder());

        when(em.createQuery(anyString(), eq(CustomerOrder.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter("customerId", customerId)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(expectedOrders);

        List<CustomerOrder> result = orderDao.findByCustomerId(customerId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(typedQuery).setParameter("customerId", customerId);
        verify(typedQuery).getResultList();
    }

    @Test
    void findById_returnsOrder_whenExists() {
        Integer orderId = 1;
        CustomerOrder expectedOrder = new CustomerOrder();
        expectedOrder.setOrderId(orderId);

        when(em.find(CustomerOrder.class, orderId)).thenReturn(expectedOrder);

        CustomerOrder result = orderDao.findById(orderId);

        assertNotNull(result);
        assertEquals(orderId, result.getOrderId());
        verify(em).find(CustomerOrder.class, orderId);
    }

    @Test
    void findById_returnsNull_whenOrderDoesNotExist() {
        Integer orderId = 999;

        when(em.find(CustomerOrder.class, orderId)).thenReturn(null);

        CustomerOrder result = orderDao.findById(orderId);

        assertNull(result);
        verify(em).find(CustomerOrder.class, orderId);
    }

    @Test
    void updateStatus_updatesOrderStatus_whenOrderExists() {
        Integer orderId = 1;
        String newStatus = CustomerOrder.STATUS_CONFIRMED;
        CustomerOrder existingOrder = new CustomerOrder();
        existingOrder.setOrderId(orderId);
        existingOrder.setStatus(CustomerOrder.STATUS_PENDING);

        when(em.find(CustomerOrder.class, orderId)).thenReturn(existingOrder);
        when(em.merge(existingOrder)).thenReturn(existingOrder);

        orderDao.updateStatus(orderId, newStatus);

        assertEquals(newStatus, existingOrder.getStatus());
        verify(em).merge(existingOrder);
    }

    @Test
    void updateStatus_doesNothing_whenOrderDoesNotExist() {
        Integer orderId = 999;
        String newStatus = CustomerOrder.STATUS_CONFIRMED;

        when(em.find(CustomerOrder.class, orderId)).thenReturn(null);

        orderDao.updateStatus(orderId, newStatus);

        verify(em, never()).merge(any());
    }

    @Test
    void findByCustomerId_handlesNullCustomerId_gracefully() {
        when(em.createQuery(anyString(), eq(CustomerOrder.class))).thenReturn(typedQuery);
        when(typedQuery.setParameter(anyString(), any())).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(new ArrayList<>());

        List<CustomerOrder> result = orderDao.findByCustomerId(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}