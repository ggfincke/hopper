package dev.fincke.hopper.catalog.product;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

// * Repository
// Data-access layer for Product entities
public interface ProductRepository extends JpaRepository<Product, UUID> 
{
    // find a product by its SKU
    Product findBySku(String sku);

    // find a product by its name
    Product findByName(String name);
}
