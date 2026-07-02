package com.techmart.dao;

import com.techmart.entity.Customer;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;

@Stateless
public class CustomerDao {

    @PersistenceContext(unitName = "techmartPU")
    private EntityManager em;

    public Customer findById(Integer customerId) {
        return em.find(Customer.class, customerId);
    }

    public Customer findByEmail(String email) {
        TypedQuery<Customer> query = em.createQuery(
                "SELECT c FROM Customer c WHERE c.email = :email", Customer.class
        );
        query.setParameter("email", email);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Customer save(Customer customer) {
        em.persist(customer);
        return customer;
    }

    public List<Customer> findAll() {
        TypedQuery<Customer> query = em.createQuery(
                "SELECT c FROM Customer c ORDER BY c.createdAt DESC",
                Customer.class
        );
        return query.getResultList();
    }

    public long countAll() {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(c) FROM Customer c",
                Long.class
        );
        return query.getSingleResult();
    }

    public List<Customer> search(String keyword) {
        String searchPattern = "%" + keyword.toLowerCase() + "%";
        TypedQuery<Customer> query = em.createQuery(
                "SELECT c FROM Customer c WHERE " +
                        "LOWER(c.firstName) LIKE :keyword OR " +
                        "LOWER(c.lastName) LIKE :keyword OR " +
                        "LOWER(c.email) LIKE :keyword " +
                        "ORDER BY c.createdAt DESC",
                Customer.class
        );
        query.setParameter("keyword", searchPattern);
        return query.getResultList();
    }
}