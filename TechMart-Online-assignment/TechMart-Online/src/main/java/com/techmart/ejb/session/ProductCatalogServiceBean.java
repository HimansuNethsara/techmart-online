package com.techmart.ejb.session;

import com.techmart.dao.ProductDao;
import com.techmart.entity.Product;
import com.techmart.util.LifecycleLogger;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Stateless;

import java.util.List;
import java.util.logging.Logger;

@Stateless
@Local(ProductCatalogService.class)
public class ProductCatalogServiceBean implements ProductCatalogService {

    private static final Logger LOGGER = Logger.getLogger(ProductCatalogServiceBean.class.getName());

    @EJB
    private ProductDao productDao;

    @PostConstruct
    public void init() {
        LifecycleLogger.log(LOGGER, ProductCatalogServiceBean.class, "PostConstruct (pooled instance created)", this);
    }

    @PreDestroy
    public void cleanup() {
        LifecycleLogger.log(LOGGER, ProductCatalogServiceBean.class, "PreDestroy (pooled instance destroyed)", this);
    }

    @Override
    public Product getProduct(Integer productId) {
        return productDao.findById(productId);
    }

    @Override
    public List<Product> getAllActiveProducts() {
        return productDao.findAllActive();
    }

    @Override
    public List<Product> getProductsByCategory(Integer categoryId) {
        return productDao.findByCategory(categoryId);
    }

    @Override
    public List<Product> searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllActiveProducts();
        }
        return productDao.searchByName(keyword.trim());
    }
}