package com.techmart.dao;

import com.techmart.entity.Inventory;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Stateless
public class InventoryDao {

    @PersistenceContext(unitName = "techmartPU")
    private EntityManager em;

    public Map<Integer, Integer> loadAllProductTotals() {
        TypedQuery<Object[]> query = em.createQuery(
                "SELECT i.product.productId, SUM(i.quantityOnHand) " +
                        "FROM Inventory i GROUP BY i.product.productId",
                Object[].class
        );
        return query.getResultList().stream()
                .collect(Collectors.toMap(
                        row -> (Integer) row[0],
                        row -> ((Number) row[1]).intValue()
                ));
    }

    public void adjustFirstWarehouseStock(Integer productId, int delta) {
        TypedQuery<Inventory> query = em.createQuery(
                "SELECT i FROM Inventory i WHERE i.product.productId = :productId",
                Inventory.class
        );
        query.setParameter("productId", productId);
        List<Inventory> rows = query.setMaxResults(1).getResultList();
        if (!rows.isEmpty()) {
            Inventory inv = rows.get(0);
            inv.setQuantityOnHand(inv.getQuantityOnHand() + delta);
            em.merge(inv);
        }

    }

    public Inventory createInventory(com.techmart.entity.Product product,
                                     com.techmart.entity.Warehouse warehouse,
                                     int quantityOnHand,
                                     int reorderThreshold) {
        Inventory inv = new Inventory();
        inv.setProduct(product);
        inv.setWarehouse(warehouse);
        inv.setQuantityOnHand(quantityOnHand);
        inv.setReorderThreshold(reorderThreshold);
        em.persist(inv);
        return inv;
    }

    public List<Inventory> findByProductId(Integer productId) {
        TypedQuery<Inventory> query = em.createQuery(
                "SELECT i FROM Inventory i WHERE i.product.productId = :productId",
                Inventory.class
        );
        query.setParameter("productId", productId);
        return query.getResultList();
    }

}