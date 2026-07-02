package com.techmart.dao;

import com.techmart.entity.Warehouse;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;

@Stateless
public class WarehouseDao {

    @PersistenceContext(unitName = "techmartPU")
    private EntityManager em;

    public Warehouse findById(Integer warehouseId) {
        return em.find(Warehouse.class, warehouseId);
    }

    public List<Warehouse> findAll() {
        TypedQuery<Warehouse> query = em.createQuery(
                "SELECT w FROM Warehouse w ORDER BY w.name",
                Warehouse.class
        );
        return query.getResultList();
    }

    public Warehouse save(Warehouse warehouse) {
        em.persist(warehouse);
        return warehouse;
    }
}