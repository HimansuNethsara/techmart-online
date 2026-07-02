package com.techmart.dao;

import com.techmart.entity.Cart;
import com.techmart.entity.CartItem;
import com.techmart.entity.Customer;
import com.techmart.entity.Product;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Stateless
public class CartDao {

    @PersistenceContext(unitName = "techmartPU")
    private EntityManager em;

    public Cart findActiveCartByCustomer(Integer customerId) {
        TypedQuery<Cart> query = em.createQuery(
                "SELECT DISTINCT c FROM Cart c " +
                        "LEFT JOIN FETCH c.items i " +
                        "LEFT JOIN FETCH i.product " +
                        "WHERE c.customer.customerId = :customerId " +
                        "AND c.status = :status",
                Cart.class
        );
        query.setParameter("customerId", customerId);
        query.setParameter("status", Cart.STATUS_ACTIVE);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Cart findAnyCartByCustomer(Integer customerId) {
        TypedQuery<Cart> query = em.createQuery(
                "SELECT DISTINCT c FROM Cart c " +
                        "LEFT JOIN FETCH c.items i " +
                        "LEFT JOIN FETCH i.product " +
                        "WHERE c.customer.customerId = :customerId",
                Cart.class
        );
        query.setParameter("customerId", customerId);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Cart reactivateCart(Cart cart) {
        for (CartItem item : new java.util.ArrayList<>(cart.getItems())) {
            em.remove(em.contains(item) ? item : em.merge(item));
        }
        cart.getItems().clear();
        cart.setStatus(Cart.STATUS_ACTIVE);
        return em.merge(cart);
    }

    public Cart createCart(Integer customerId) {
        Customer customerRef = em.getReference(Customer.class, customerId);
        Cart cart = new Cart();
        cart.setCustomer(customerRef);
        cart.setStatus(Cart.STATUS_ACTIVE);
        em.persist(cart);
        return cart;
    }

    public CartItem addItem(Cart cart, Integer productId, int quantity) {
        TypedQuery<CartItem> query = em.createQuery(
                "SELECT ci FROM CartItem ci WHERE ci.cart.cartId = :cartId " +
                        "AND ci.product.productId = :productId",
                CartItem.class
        );
        query.setParameter("cartId", cart.getCartId());
        query.setParameter("productId", productId);

        CartItem existing;
        try {
            existing = query.getSingleResult();
        } catch (NoResultException e) {
            existing = null;
        }

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + quantity);
            return em.merge(existing);
        }

        Product productRef = em.getReference(Product.class, productId);
        CartItem item = new CartItem();
        item.setCart(cart);
        item.setProduct(productRef);
        item.setQuantity(quantity);
        em.persist(item);
        return item;
    }

    public void updateItemQuantity(CartItem item, int newQuantity) {
        item.setQuantity(newQuantity);
        em.merge(item);
    }

    public void removeItem(CartItem item) {
        CartItem managed = em.find(CartItem.class, item.getCartItemId());
        if (managed != null) {
            em.remove(managed);
        }
    }

    public void touchCart(Cart cart) {
        em.merge(cart);
    }
}