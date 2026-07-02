package com.techmart.dao;

import com.techmart.entity.Product;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;

@Stateless
public class ProductDao {

    @PersistenceContext(unitName = "techmartPU")
    private EntityManager em;

    public Product findById(Integer productId) {
        return em.find(Product.class, productId);
    }

    public List<Product> findAllActive() {
        TypedQuery<Product> query = em.createQuery(
                "SELECT p FROM Product p WHERE p.active = true ORDER BY p.name",
                Product.class
        );
        return query.getResultList();
    }

    public List<Product> findByCategory(Integer categoryId) {
        TypedQuery<Product> query = em.createQuery(
                "SELECT p FROM Product p WHERE p.category.categoryId = :categoryId " +
                        "AND p.active = true ORDER BY p.name",
                Product.class
        );
        query.setParameter("categoryId", categoryId);
        return query.getResultList();
    }

    public List<Product> searchByName(String keyword) {
        TypedQuery<Product> query = em.createQuery(
                "SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(:keyword) " +
                        "AND p.active = true ORDER BY p.name",
                Product.class
        );
        query.setParameter("keyword", "%" + keyword + "%");
        return query.getResultList();
    }

    public Product save(Product product) {
        if (product.getProductId() == null) {
            em.persist(product);
            return product;
        } else {
            return em.merge(product);
        }
    }

}