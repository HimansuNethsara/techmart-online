package com.techmart.ejb.session;

import com.techmart.async.OrderNotificationService;
import com.techmart.dao.CartDao;
import com.techmart.dao.CustomerDao;
import com.techmart.dao.OrderDao;
import com.techmart.entity.Cart;
import com.techmart.entity.CartItem;
import com.techmart.entity.Customer;
import com.techmart.entity.CustomerOrder;
import com.techmart.jms.OrderPlacedMessage;
import com.techmart.jms.OrderQueueProducer;
import com.techmart.util.LifecycleLogger;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Stateful;
import jakarta.ejb.PostActivate;
import jakarta.ejb.PrePassivate;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Logger;

@Stateful
@Local(ShoppingCartService.class)
public class ShoppingCartServiceBean implements ShoppingCartService, Serializable {

    private static final Logger LOGGER = Logger.getLogger(ShoppingCartServiceBean.class.getName());

    @EJB
    private CartDao cartDao;

    @EJB
    private CustomerDao customerDao;

    @EJB
    private OrderDao orderDao;

    @EJB
    private OrderNotificationService orderNotificationService;

    @EJB
    private OrderQueueProducer orderQueueProducer;

    private Cart cart;

    @PostConstruct
    public void init() {
        LifecycleLogger.log(LOGGER, ShoppingCartServiceBean.class,
                "PostConstruct (new client session bean created)", this);
    }

    @PreDestroy
    public void cleanup() {
        LifecycleLogger.log(LOGGER, ShoppingCartServiceBean.class,
                "PreDestroy (session ended, bean destroyed)", this);
    }

    @PrePassivate
    public void prePassivate() {
        LifecycleLogger.log(LOGGER, ShoppingCartServiceBean.class,
                "PrePassivate (serializing instance to free memory, cartId="
                        + (cart != null ? cart.getCartId() : "none") + ")", this);
    }

    @PostActivate
    public void postActivate() {
        LifecycleLogger.log(LOGGER, ShoppingCartServiceBean.class,
                "PostActivate (reactivated from passivated state, cartId="
                        + (cart != null ? cart.getCartId() : "none") + ")", this);
    }

    @Override
    public void initCart(Integer customerId) {
        Customer customer = customerDao.findById(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("No customer found for id " + customerId);
        }

        Cart anyCart = cartDao.findAnyCartByCustomer(customerId);

        if (anyCart == null) {
            this.cart = cartDao.createCart(customerId);
        } else if (Cart.STATUS_ACTIVE.equals(anyCart.getStatus())) {
            this.cart = anyCart;
        } else {
            this.cart = cartDao.reactivateCart(anyCart);
        }
    }

    @Override
    public void addItem(Integer productId, int quantity) {
        requireCart();
        cartDao.addItem(cart, productId, quantity);
        refreshCart();
    }

    @Override
    public void updateQuantity(Integer cartItemId, int newQuantity) {
        requireCart();
        CartItem item = findItemOrThrow(cartItemId);
        cartDao.updateItemQuantity(item, newQuantity);
        refreshCart();
    }

    @Override
    public void removeItem(Integer cartItemId) {
        requireCart();
        CartItem item = findItemOrThrow(cartItemId);
        cartDao.removeItem(item);
        refreshCart();
    }

    @Override
    public List<CartItem> getItems() {
        if (cart == null || Cart.STATUS_CHECKED_OUT.equals(cart.getStatus())) {
            return java.util.Collections.emptyList();
        }
        return cart.getItems();
    }

    @Override
    public BigDecimal getCartTotal() {
        if (cart == null || Cart.STATUS_CHECKED_OUT.equals(cart.getStatus())) {
            return BigDecimal.ZERO;
        }

        return cart.getItems().stream()
                .map(item -> item.getProduct().getBasePrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public Integer checkout() {
        requireCart();
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot checkout an empty cart.");
        }
        CustomerOrder order = orderDao.placeOrder(cart, cart.getItems());
        Integer newOrderId = order.getOrderId();
        Integer customerId = cart.getCustomer().getCustomerId();
        String customerEmail = cart.getCustomer().getEmail();

        List<OrderPlacedMessage.LineItem> lineItems = cart.getItems().stream()
                .map(ci -> new OrderPlacedMessage.LineItem(
                        ci.getProduct().getProductId(), ci.getQuantity()))
                .collect(java.util.stream.Collectors.toList());

        orderQueueProducer.sendOrderPlaced(new OrderPlacedMessage(newOrderId, lineItems));

        // Send notification with customer ID
        orderNotificationService.sendOrderConfirmation(
                newOrderId, customerId, customerEmail);

        cart.getItems().clear();
        cart.setStatus(Cart.STATUS_CHECKED_OUT);
        return newOrderId;
    }

    private void refreshCart() {
        if (cart == null) return;

        this.cart = cartDao.findActiveCartByCustomer(
                cart.getCustomer().getCustomerId()
        );
    }

    private void requireCart() {
        if (cart == null) {
            throw new IllegalStateException("initCart(customerId) must be called before using the cart.");
        }
    }

    private CartItem findItemOrThrow(Integer cartItemId) {
        return cart.getItems().stream()
                .filter(i -> i.getCartItemId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cart item " + cartItemId + " not found in this cart."));
    }
}