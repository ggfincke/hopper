package dev.fincke.hopper.catalog.product;

import dev.fincke.hopper.catalog.product.dto.ProductCreateRequest;
import dev.fincke.hopper.catalog.product.dto.ProductResponse;
import dev.fincke.hopper.catalog.product.dto.ProductUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

// REST controller handling product API endpoints
@RestController
@RequestMapping("/api/products")
public class ProductController
{
    // * Dependencies
    
    // Spring will inject service dependency
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
    public Page<ProductResponse> listProducts(@PageableDefault(size = 20) Pageable pageable)
    {
        return productService.findAll(pageable);
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
    public Page<ProductResponse> searchProducts(@RequestParam String name,
                                                    @PageableDefault(size = 20) Pageable pageable)
    {
        return productService.findByNameContaining(name, pageable);
    }
    
    // GET /api/products/low-stock - find low stock products
    @GetMapping("/low-stock")
    public Page<ProductResponse> getLowStockProducts(@RequestParam(defaultValue = "10") int threshold,
                                                   @PageableDefault(size = 20) Pageable pageable)
    {
        return productService.findLowStockProducts(threshold, pageable);
    }
    
    // * Stock Management Endpoints
    
    // PATCH /api/products/{id}/stock - adjust stock quantity
    @PatchMapping("/{id}/stock")
    public ProductResponse adjustStock(@PathVariable UUID id, @RequestParam int adjustment)
    {
        return productService.adjustStock(id, adjustment);
    }
}
