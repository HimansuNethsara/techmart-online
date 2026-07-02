package com.techmart.dao;

import com.techmart.entity.*;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.math.BigDecimal;
import java.util.List;

@Stateless
public class OrderDao {

    @PersistenceContext(unitName = "techmartPU")
    private EntityManager em;

    public CustomerOrder placeOrder(Cart cart, List<CartItem> items) {
        CustomerOrder order = new CustomerOrder();
        order.setCustomer(cart.getCustomer());
        order.setStatus(CustomerOrder.STATUS_PENDING);

        BigDecimal total = BigDecimal.ZERO;

        for (CartItem ci : items) {
            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProduct(ci.getProduct());
            oi.setQuantity(ci.getQuantity());

            BigDecimal unitPrice = ci.getProduct().getBasePrice();
            oi.setUnitPrice(unitPrice);

            total = total.add(unitPrice.multiply(BigDecimal.valueOf(ci.getQuantity())));
            order.getItems().add(oi);
        }

        order.setTotalAmount(total);
        em.persist(order);
        em.flush();

        cart.setStatus(Cart.STATUS_CHECKED_OUT);
        em.merge(cart);

        return order;
    }

    public List<CustomerOrder> findByCustomerId(Integer customerId) {
        TypedQuery<CustomerOrder> query = em.createQuery(
                "SELECT o FROM CustomerOrder o " +
                        "WHERE o.customer.customerId = :customerId " +
                        "ORDER BY o.createdAt DESC",
                CustomerOrder.class
        );
        query.setParameter("customerId", customerId);
        return query.getResultList();
    }

    public CustomerOrder findById(Integer orderId) {
        return em.find(CustomerOrder.class, orderId);
    }

    public void updateStatus(Integer orderId, String status) {
        CustomerOrder order = findById(orderId);
        if (order != null) {
            order.setStatus(status);
            em.merge(order);
        }
    }
}