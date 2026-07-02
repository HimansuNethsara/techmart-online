package com.techmart.arquillian;

import com.techmart.ejb.session.ProductCatalogService;
import com.techmart.entity.Product;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.naming.InitialContext;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ArquillianExtension.class)
class ProductCatalogServiceBeanArquillianTest {

    private static final String JNDI_NAME =
            "java:global/catalog-test/ProductCatalogServiceBean!com.techmart.ejb.session.ProductCatalogService";

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "catalog-test.war")
                .addPackages(true, "com.techmart")
                .addAsResource("META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    private ProductCatalogService catalogService;

    @BeforeEach
    void lookupCatalogService() throws Exception {
        InitialContext ctx = new InitialContext();
        catalogService = (ProductCatalogService) ctx.lookup(JNDI_NAME);
        assertNotNull(catalogService,
                "JNDI lookup returned null for " + JNDI_NAME +
                        " — check Payara's deployment log for the bean's ACTUAL printed " +
                        "JNDI name and update JNDI_NAME above to match exactly.");
    }

    @Test
    void getAllActiveProducts_returnsOnlyActiveProducts() {
        List<Product> products = catalogService.getAllActiveProducts();

        assertNotNull(products);
        assertFalse(products.isEmpty(),
                "Expected at least one active product in the database — " +
                        "seed test data via MySQL Workbench if this fails.");

        for (Product p : products) {
            assertTrue(p.getActive(),
                    "getAllActiveProducts() must never return an inactive product, but found: "
                            + p.getName());
        }
    }

    @Test
    void getProduct_returnsCorrectProduct_byRealId() {
        List<Product> products = catalogService.getAllActiveProducts();
        assertFalse(products.isEmpty(), "Need at least one product to run this test.");
        Integer realId = products.get(0).getProductId();

        Product fetched = catalogService.getProduct(realId);

        assertNotNull(fetched);
        assertEquals(realId, fetched.getProductId());
    }

    @Test
    void getProduct_returnsNull_forNonExistentId() {
        Product fetched = catalogService.getProduct(999999);
        assertNull(fetched);
    }

    @Test
    void searchProducts_findsMatch_byPartialNameCaseInsensitive() {
        List<Product> all = catalogService.getAllActiveProducts();
        assertFalse(all.isEmpty(), "Need at least one product to run this test.");

        String realName = all.get(0).getName();
        String partialLowercase = realName.substring(0, Math.min(3, realName.length())).toLowerCase();

        List<Product> results = catalogService.searchProducts(partialLowercase);

        assertFalse(results.isEmpty(),
                "Searching for a lowercase substring of a real product name ('"
                        + partialLowercase + "') should find at least one match — "
                        + "confirms the search query is genuinely case-insensitive.");
    }

    @Test
    void searchProducts_returnsAllActiveProducts_whenKeywordIsBlank() {
        List<Product> blankSearch = catalogService.searchProducts("   ");
        List<Product> allActive = catalogService.getAllActiveProducts();

        assertEquals(allActive.size(), blankSearch.size(),
                "A blank/whitespace-only keyword should fall back to the full active catalog.");
    }
}