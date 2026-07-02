package com.techmart.ejb.session;

import com.techmart.async.OrderNotificationService;
import com.techmart.dao.CartDao;
import com.techmart.dao.CustomerDao;
import com.techmart.dao.OrderDao;
import com.techmart.entity.Cart;
import com.techmart.entity.CartItem;
import com.techmart.entity.Customer;
import com.techmart.entity.CustomerOrder;
import com.techmart.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ShoppingCartServiceBeanTest {

    @Mock
    private CartDao cartDao;

    @Mock
    private CustomerDao customerDao;

    @Mock
    private OrderDao orderDao;

    @Mock
    private OrderNotificationService orderNotificationService;

    @Mock
    private com.techmart.jms.OrderQueueProducer orderQueueProducer;

    @InjectMocks
    private ShoppingCartServiceBean cartService;

    private Customer testCustomer;
    private Cart testCart;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer();
        testCustomer.setCustomerId(1);
        testCustomer.setEmail("test@techmart.com");

        testCart = new Cart();
        testCart.setCartId(100);
        testCart.setCustomer(testCustomer);
        testCart.setStatus(Cart.STATUS_ACTIVE);
        testCart.setItems(new ArrayList<>());
    }

    @Test
    void getItems_returnsEmptyList_whenInitCartNeverCalled() {

        List<CartItem> items = cartService.getItems();

        assertNotNull(items);
        assertTrue(items.isEmpty());
    }

    @Test
    void addItem_throwsIllegalState_whenInitCartNeverCalled() {
        assertThrows(IllegalStateException.class,
                () -> cartService.addItem(1, 1));
    }

    @Test
    void initCart_createsNewCart_whenCustomerHasNoExistingCart() {
        when(customerDao.findById(1)).thenReturn(testCustomer);
        when(cartDao.findAnyCartByCustomer(1)).thenReturn(null);
        when(cartDao.createCart(1)).thenReturn(testCart);

        cartService.initCart(1);

        verify(cartDao).createCart(1);
        verify(cartDao, never()).reactivateCart(any());
    }

    @Test
    void initCart_reusesActiveCart_whenOneAlreadyExists() {
        when(customerDao.findById(1)).thenReturn(testCustomer);
        when(cartDao.findAnyCartByCustomer(1)).thenReturn(testCart); // status ACTIVE

        cartService.initCart(1);

        verify(cartDao, never()).createCart(anyInt());
        verify(cartDao, never()).reactivateCart(any());
    }

    @Test
    void initCart_reactivatesCart_whenExistingCartIsCheckedOut() {
        testCart.setStatus(Cart.STATUS_CHECKED_OUT);
        when(customerDao.findById(1)).thenReturn(testCustomer);
        when(cartDao.findAnyCartByCustomer(1)).thenReturn(testCart);
        when(cartDao.reactivateCart(testCart)).thenReturn(testCart);

        cartService.initCart(1);

        verify(cartDao).reactivateCart(testCart);
        verify(cartDao, never()).createCart(anyInt());
    }

    @Test
    void initCart_throwsIllegalArgument_whenCustomerDoesNotExist() {
        when(customerDao.findById(999)).thenReturn(null);

        assertThrows(IllegalArgumentException.class,
                () -> cartService.initCart(999));
    }

    @Test
    void checkout_throwsIllegalState_whenCartIsEmpty() {
        when(customerDao.findById(1)).thenReturn(testCustomer);
        when(cartDao.findAnyCartByCustomer(1)).thenReturn(null);
        when(cartDao.createCart(1)).thenReturn(testCart); // items list is empty
        cartService.initCart(1);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                cartService::checkout
        );
        assertTrue(ex.getMessage().toLowerCase().contains("empty"));

        verify(orderDao, never()).placeOrder(any(), any());
    }

    @Test
    void checkout_placesOrderAndClearsCart_whenItemsArePresent() {
        Product product = new Product();
        product.setProductId(5);
        product.setBasePrice(new BigDecimal("100.00"));

        CartItem item = new CartItem();
        item.setCartItemId(1);
        item.setProduct(product);
        item.setQuantity(2);

        List<CartItem> items = new ArrayList<>();
        items.add(item);
        testCart.setItems(items);

        when(customerDao.findById(1)).thenReturn(testCustomer);
        when(cartDao.findAnyCartByCustomer(1)).thenReturn(testCart);
        cartService.initCart(1);

        CustomerOrder placedOrder = new CustomerOrder();
        placedOrder.setOrderId(42);
        when(orderDao.placeOrder(eq(testCart), eq(items))).thenReturn(placedOrder);
        when(orderNotificationService.sendOrderConfirmation(anyInt(), anyInt(), anyString()))
                .thenReturn(mock(Future.class));

        Integer orderId = cartService.checkout();

        assertEquals(42, orderId);
        verify(orderDao).placeOrder(testCart, items);
        verify(orderQueueProducer).sendOrderPlaced(any());
        // Updated to include customerId parameter
        verify(orderNotificationService).sendOrderConfirmation(42, testCustomer.getCustomerId(), "test@techmart.com");

        assertTrue(cartService.getItems().isEmpty());
        assertEquals(BigDecimal.ZERO, cartService.getCartTotal());
    }

    @Test
    void getCartTotal_sumsUnitPriceTimesQuantity_acrossMultipleItems() {
        Product productA = new Product();
        productA.setBasePrice(new BigDecimal("50.00"));
        CartItem itemA = new CartItem();
        itemA.setProduct(productA);
        itemA.setQuantity(2); // 100.00

        Product productB = new Product();
        productB.setBasePrice(new BigDecimal("30.00"));
        CartItem itemB = new CartItem();
        itemB.setProduct(productB);
        itemB.setQuantity(3); // 90.00

        List<CartItem> items = new ArrayList<>();
        items.add(itemA);
        items.add(itemB);
        testCart.setItems(items);

        when(customerDao.findById(1)).thenReturn(testCustomer);
        when(cartDao.findAnyCartByCustomer(1)).thenReturn(testCart);
        cartService.initCart(1);

        BigDecimal total = cartService.getCartTotal();

        assertEquals(new BigDecimal("190.00"), total);
    }
}