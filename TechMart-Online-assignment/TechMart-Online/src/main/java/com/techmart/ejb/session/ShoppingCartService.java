package com.techmart.ejb.session;

import com.techmart.entity.CartItem;

import java.math.BigDecimal;
import java.util.List;

public interface ShoppingCartService {

    void initCart(Integer customerId);

    void addItem(Integer productId, int quantity);

    void updateQuantity(Integer cartItemId, int newQuantity);

    void removeItem(Integer cartItemId);

    List<CartItem> getItems();

    BigDecimal getCartTotal();

    Integer checkout();
}