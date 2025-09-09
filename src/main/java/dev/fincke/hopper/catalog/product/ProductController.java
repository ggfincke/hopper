package dev.fincke.hopper.catalog.product;

import dev.fincke.hopper.catalog.product.dto.ProductCreateRequest;
import dev.fincke.hopper.catalog.product.dto.ProductResponse;
import dev.fincke.hopper.catalog.product.dto.ProductUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for product operations.
 * 
 * Provides CRUD endpoints for product management with proper validation
 * and error handling. Uses service layer for business logic.
 */
@RestController
@RequestMapping("/api/products")
public class ProductController
{
    // * Dependencies
    
    // service for product business logic
    private final ProductService productService;

    // * Constructor
    public ProductController(ProductService productService)
    {
        this.productService = productService;
    }

    // * CRUD Endpoints
    
    // POST /api/products - create new product
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductCreateRequest request)
    {
        ProductResponse response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    // GET /api/products - list all products
    @GetMapping
    public List<ProductResponse> listProducts()
    {
        return productService.findAll();
    }
    
    // GET /api/products/{id} - get product by ID
    @GetMapping("/{id}")
    public ProductResponse getProduct(@PathVariable UUID id)
    {
        return productService.findById(id);
    }
    
    // PUT /api/products/{id} - update existing product
    @PutMapping("/{id}")
    public ProductResponse updateProduct(@PathVariable UUID id, @Valid @RequestBody ProductUpdateRequest request)
    {
        return productService.updateProduct(id, request);
    }
    
    // DELETE /api/products/{id} - delete product
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id)
    {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
    
    // * Query Endpoints
    
    // GET /api/products/sku/{sku} - find product by SKU
    @GetMapping("/sku/{sku}")
    public ProductResponse getProductBySku(@PathVariable String sku)
    {
        return productService.findBySku(sku);
    }
    
    // GET /api/products/search - search products by name
    @GetMapping("/search")
    public List<ProductResponse> searchProducts(@RequestParam String name)
    {
        return productService.findByNameContaining(name);
    }
    
    // GET /api/products/low-stock - find low stock products
    @GetMapping("/low-stock")
    public List<ProductResponse> getLowStockProducts(@RequestParam(defaultValue = "10") int threshold)
    {
        return productService.findLowStockProducts(threshold);
    }
    
    // * Stock Management Endpoints
    
    // PATCH /api/products/{id}/stock - adjust stock quantity
    @PatchMapping("/{id}/stock")
    public ProductResponse adjustStock(@PathVariable UUID id, @RequestParam int adjustment)
    {
        return productService.adjustStock(id, adjustment);
    }
}
