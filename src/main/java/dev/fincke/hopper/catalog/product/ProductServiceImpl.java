package dev.fincke.hopper.catalog.product;

import dev.fincke.hopper.catalog.product.dto.ProductCreateRequest;
import dev.fincke.hopper.catalog.product.dto.ProductResponse;
import dev.fincke.hopper.catalog.product.dto.ProductUpdateRequest;
import dev.fincke.hopper.catalog.product.exception.DuplicateSkuException;
import dev.fincke.hopper.catalog.product.exception.InsufficientStockException;
import dev.fincke.hopper.catalog.product.exception.ProductNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service implementation for product business operations.
 * 
 * Handles all product-related business logic including validation, 
 * inventory management, and data transformation between entities and DTOs.
 */
@Service
@Transactional(readOnly = true) // default to read-only transactions
public class ProductServiceImpl implements ProductService 
{
    // * Dependencies
    
    // repository for product data access
    private final ProductRepository productRepository;
    
    // * Constructor
    
    public ProductServiceImpl(ProductRepository productRepository) 
    {
        this.productRepository = productRepository;
    }
    
    // * Core CRUD Operations
    
    @Override
    @Transactional // write operation, requires full transaction
    public ProductResponse createProduct(ProductCreateRequest request) 
    {
        // validate SKU uniqueness if provided
        if (request.sku() != null && !request.sku().trim().isEmpty()) 
        {
            if (existsBySku(request.sku().trim())) 
            {
                throw new DuplicateSkuException(request.sku().trim());
            }
        }
        
        // create and populate entity
        Product product = new Product(
            request.sku(),
            request.name(),
            request.price()
        );
        
        // set optional fields
        if (request.description() != null) 
        {
            product.setDescription(request.description());
        }
        
        product.setQuantity(Math.max(0, request.quantity())); // ensure non-negative
        
        // save and return response
        Product savedProduct = productRepository.save(product);
        return ProductResponse.from(savedProduct);
    }
    
    @Override
    @Transactional
    public ProductResponse updateProduct(UUID id, ProductUpdateRequest request) 
    {
        // find existing product
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException(id));
        
        // validate that at least one field is provided for update
        if (!request.hasUpdates()) 
        {
            throw new IllegalArgumentException("At least one field must be provided for update");
        }
        
        // update SKU if provided and different
        if (request.sku() != null && !request.sku().equals(product.getSku())) 
        {
            if (existsBySku(request.sku())) 
            {
                throw new DuplicateSkuException(request.sku());
            }
            product.setSku(request.sku());
        }
        
        // update name if provided
        if (request.name() != null) 
        {
            product.setName(request.name());
        }
        
        // update description if provided
        if (request.description() != null) 
        {
            product.setDescription(request.description());
        }
        
        // update price if provided
        if (request.price() != null) 
        {
            product.setPrice(request.price()); // BigDecimal scaling handled in entity setter
        }
        
        // update quantity if provided
        if (request.quantity() != null) 
        {
            product.setQuantity(Math.max(0, request.quantity())); // ensure non-negative
        }
        
        Product savedProduct = productRepository.save(product);
        return ProductResponse.from(savedProduct);
    }
    
    @Override
    public ProductResponse findById(UUID id) 
    {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException(id));
        return ProductResponse.from(product);
    }
    
    @Override
    public List<ProductResponse> findAll() 
    {
        return productRepository.findAll().stream()
            .map(ProductResponse::from)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void deleteProduct(UUID id) 
    {
        if (!productRepository.existsById(id)) 
        {
            throw new ProductNotFoundException(id);
        }
        
        // TODO: check for dependencies (listings, order items) before deletion
        productRepository.deleteById(id);
    }
    
    // * Stock Management Operations
    
    @Override
    @Transactional
    public ProductResponse adjustStock(UUID id, int quantityChange) 
    {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException(id));
        
        int newQuantity = product.getQuantity() + quantityChange;
        
        // prevent negative stock
        if (newQuantity < 0) 
        {
            throw new InsufficientStockException(id, product.getQuantity(), Math.abs(quantityChange));
        }
        
        product.setQuantity(newQuantity);
        Product savedProduct = productRepository.save(product);
        return ProductResponse.from(savedProduct);
    }
    
    
    // * Query Operations
    
    @Override
    public List<ProductResponse> findLowStockProducts(int threshold) 
    {
        return productRepository.findByQuantityLessThanEqual(threshold).stream()
            .map(ProductResponse::from)
            .collect(Collectors.toList());
    }
    
    @Override
    public ProductResponse findBySku(String sku) 
    {
        Product product = productRepository.findBySku(sku.trim())
            .orElseThrow(() -> new ProductNotFoundException(sku));
        return ProductResponse.from(product);
    }
    
    // checks if a product exists by SKU (private helper for internal validation)
    private boolean existsBySku(String sku) 
    {
        return productRepository.existsBySku(sku.trim());
    }
    
    @Override
    public List<ProductResponse> findByNameContaining(String name) 
    {
        return productRepository.findByNameContainingIgnoreCase(name.trim()).stream()
            .map(ProductResponse::from)
            .collect(Collectors.toList());
    }
}