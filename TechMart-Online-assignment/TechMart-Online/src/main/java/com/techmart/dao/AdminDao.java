package com.techmart.dao;

import com.techmart.entity.Admin;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Stateless
public class AdminDao {

    @PersistenceContext(unitName = "techmartPU")
    private EntityManager em;

    public Admin findById(Integer adminId) {
        return em.find(Admin.class, adminId);
    }

    public Admin findByEmail(String email) {
        TypedQuery<Admin> query = em.createQuery(
                "SELECT a FROM Admin a WHERE a.email = :email", Admin.class
        );
        query.setParameter("email", email);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Admin save(Admin admin) {
        em.persist(admin);
        return admin;
    }
}