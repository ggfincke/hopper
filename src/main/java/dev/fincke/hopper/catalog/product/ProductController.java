package dev.fincke.hopper.catalog.product;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController
{
    // * Dependencies
    // repository to access products
    private final ProductRepository repo;

    // * Constructor
    public ProductController(ProductRepository repo)
    {
        this.repo = repo;
    }

    // * Routes
    // GET /api/products - list all products
    @GetMapping
    public List<ProductDto> list() 
    {
        return repo.findAll().stream()
                .map(p -> new ProductDto(
                        p.getId().toString(),
                        p.getSku(),
                        p.getName(),
                        p.getDescription(),
                        p.getPrice(),
                        p.getQuantity()))
                .collect(Collectors.toList());
    }

    // * DTO
    // Represents a product in API responses
    static class ProductDto 
    {
        // product ID
        private final String id;
        // SKU of product
        private final String sku;
        // name of product
        private final String name;
        // optional description
        private final String description;
        // price of product
        private final BigDecimal price;
        // quantity of product in stock
        private final int quantity;

        // * Constructor
        public ProductDto(String id, String sku, String name, String description, BigDecimal price, int quantity)
        {
            this.id = id;
            this.sku = sku;
            this.name = name;
            this.description = description;
            this.price = price;
            this.quantity = quantity;
        }

        // * Getters (for serialization)
        // product ID
        public String getId()
        {
            return id;
        }

        // SKU
        public String getSku()
        {
            return sku;
        }

        // name
        public String getName()
        {
            return name;
        }

        // description
        public String getDescription()
        {
            return description;
        }

        // price
        public BigDecimal getPrice()
        {
            return price;
        }

        // quantity
        public int getQuantity()
        {
            return quantity;
        }
    }
}
