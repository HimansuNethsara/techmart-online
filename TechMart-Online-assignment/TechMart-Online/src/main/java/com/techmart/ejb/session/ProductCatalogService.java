package com.techmart.ejb.session;

import com.techmart.entity.Product;

import java.util.List;

public interface ProductCatalogService {

    Product getProduct(Integer productId);

    List<Product> getAllActiveProducts();

    List<Product> getProductsByCategory(Integer categoryId);

    List<Product> searchProducts(String keyword);
}