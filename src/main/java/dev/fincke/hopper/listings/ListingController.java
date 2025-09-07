package dev.fincke.hopper.listings;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/listings")
public class ListingController
{
    private final ListingRepository repo;

    public ListingController(ListingRepository repo)
    {
        this.repo = repo;
    }

    @GetMapping
    public List<ListingDto> list()
    {
        return repo.findAll().stream()
                .map(l -> new ListingDto(
                        l.getId().toString(),
                        l.getProduct().getId().toString(),
                        l.getPlatform().getId().toString(),
                        l.getExternalListingId(),
                        l.getStatus(),
                        l.getPrice(),
                        l.getQuantityListed()
                ))
                .collect(Collectors.toList());
    }

    static class ListingDto
    {
        private final String id;
        private final String productId;
        private final String platformId;
        private final String externalListingId;
        private final String status;
        private final BigDecimal price;
        private final int quantityListed;

        public ListingDto(String id,
                          String productId,
                          String platformId,
                          String externalListingId,
                          String status,
                          BigDecimal price,
                          int quantityListed)
        {
            this.id = id;
            this.productId = productId;
            this.platformId = platformId;
            this.externalListingId = externalListingId;
            this.status = status;
            this.price = price;
            this.quantityListed = quantityListed;
        }

        public String getId() { return id; }
        public String getProductId() { return productId; }
        public String getPlatformId() { return platformId; }
        public String getExternalListingId() { return externalListingId; }
        public String getStatus() { return status; }
        public BigDecimal getPrice() { return price; }
        public int getQuantityListed() { return quantityListed; }
    }
}

