package dev.fincke.hopper.catalog.product;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// * Repository
// Data-access layer for Product entities with query methods for service layer
public interface ProductRepository extends JpaRepository<Product, UUID> 
{
    // find a product by its SKU (returns Optional for null-safe handling)
    Optional<Product> findBySku(String sku);

    // find a product by its name (returns Optional for null-safe handling)
    Optional<Product> findByName(String name);
    
    // check if product with given SKU exists
    boolean existsBySku(String sku);
    
    // find products with stock at or below threshold
    List<Product> findByQuantityLessThanEqual(int quantity);
    
    // find products by partial name match (case-insensitive)
    List<Product> findByNameContainingIgnoreCase(String name);
}
