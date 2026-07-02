package com.techmart.dao;

import com.techmart.entity.Category;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@Stateless
public class CategoryDao {

    @PersistenceContext(unitName = "techmartPU")
    private EntityManager em;

    public Category create(String name, String description) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        em.persist(category);

        return category;
    }

    public Category findById(Integer categoryId) {
        return em.find(Category.class, categoryId);
    }

    public List<Category> findAll() {
        return em.createQuery("SELECT c FROM Category c", Category.class)
                .getResultList();
    }
}