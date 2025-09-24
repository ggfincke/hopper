package dev.fincke.hopper.catalog.product;

import dev.fincke.hopper.catalog.product.dto.ProductCreateRequest;
import dev.fincke.hopper.catalog.product.dto.ProductResponse;
import dev.fincke.hopper.catalog.product.dto.ProductUpdateRequest;
import dev.fincke.hopper.catalog.product.exception.DuplicateSkuException;
import dev.fincke.hopper.catalog.product.exception.InsufficientStockException;
import dev.fincke.hopper.catalog.product.exception.ProductNotFoundException;
import dev.fincke.hopper.catalog.product.exception.ProductDeletionNotAllowedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import dev.fincke.hopper.catalog.listing.ListingRepository;
import dev.fincke.hopper.order.item.OrderItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

// Service implementation for product business operations
@Service
@Transactional(readOnly = true) // default to read-only transactions
public class ProductServiceImpl implements ProductService 
{
    // * Dependencies
    
    // Spring will inject repository dependencies
    private final ProductRepository productRepository;
    private final ListingRepository listingRepository;
    private final OrderItemRepository orderItemRepository;
    
    // * Constructor
    
    public ProductServiceImpl(ProductRepository productRepository,
                              ListingRepository listingRepository,
                              OrderItemRepository orderItemRepository) 
    {
        this.productRepository = productRepository;
        this.listingRepository = listingRepository;
        this.orderItemRepository = orderItemRepository;
    }
    
    // * Core CRUD Operations
    
    @Override
    @Transactional // write operation, requires full transaction
    public ProductResponse createProduct(ProductCreateRequest request) 
    {
        // validate SKU uniqueness if provided
        String normalizedSku = normalizeSku(request.sku());
        if (normalizedSku != null)
        {
            if (existsBySku(normalizedSku))
            {
                throw new DuplicateSkuException(normalizedSku);
            }
        }
        
        // create and populate entity
        Product product = new Product(
            normalizedSku,
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
        if (request.sku() != null)
        {
            String updatedSku = normalizeSku(request.sku());
            if (!Objects.equals(updatedSku, product.getSku()))
            {
                if (existsBySku(updatedSku))
                {
                    throw new DuplicateSkuException(updatedSku);
                }
                product.setSku(updatedSku);
            }
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
    public Page<ProductResponse> findAll(Pageable pageable)
    {
        Pageable resolved = pageable == null ? Pageable.unpaged() : pageable;
        return productRepository.findAll(resolved)
            .map(ProductResponse::from);
    }
    
    @Override
    @Transactional
    public void deleteProduct(UUID id) 
    {
        if (!productRepository.existsById(id)) 
        {
            throw new ProductNotFoundException(id);
        }

        if (listingRepository.existsByProductId(id))
        {
            throw new ProductDeletionNotAllowedException(id, "active listings exist");
        }

        if (orderItemRepository.existsByListingProductId(id))
        {
            throw new ProductDeletionNotAllowedException(id, "order items reference this product");
        }

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
    public Page<ProductResponse> findLowStockProducts(int threshold, Pageable pageable)
    {
        Pageable resolved = pageable == null ? Pageable.unpaged() : pageable;
        return productRepository.findByQuantityLessThanEqual(threshold, resolved)
            .map(ProductResponse::from);
    }
    
    @Override
    public ProductResponse findBySku(String sku) 
    {
        String normalizedSku = normalizeSku(sku);
        if (normalizedSku == null)
        {
            throw new IllegalArgumentException("SKU cannot be null or blank");
        }

        Product product = productRepository.findBySku(normalizedSku)
            .orElseThrow(() -> new ProductNotFoundException(normalizedSku));
        return ProductResponse.from(product);
    }
    
    // checks if a product exists by SKU (private helper for internal validation)
    private boolean existsBySku(String sku) 
    {
        String normalizedSku = normalizeSku(sku);
        return normalizedSku != null && productRepository.existsBySku(normalizedSku);
    }

    // normalizes SKU values (null or blank -> null, trims otherwise)
    private String normalizeSku(String sku)
    {
        if (sku == null)
        {
            return null;
        }
        String normalized = sku.trim();
        return normalized.isEmpty() ? null : normalized;
    }
    
    @Override
    public List<ProductResponse> findByNameContaining(String name) 
    {
        return productRepository.findByNameContainingIgnoreCase(name.trim()).stream()
            .map(ProductResponse::from)
            .collect(Collectors.toList());
    }

    @Override
    public Page<ProductResponse> findByNameContaining(String name, Pageable pageable)
    {
        Pageable resolved = pageable == null ? Pageable.unpaged() : pageable;
        return productRepository.findByNameContainingIgnoreCase(name.trim(), resolved)
            .map(ProductResponse::from);
    }
}
