package dev.fincke.hopper.orderaddresses;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/order-addresses")
public class OrderAddressController
{
    // * Dependencies
    // repository to access order addresses
    private final OrderAddressRepository repo;

    // * Constructor
    public OrderAddressController(OrderAddressRepository repo)
    {
        this.repo = repo;
    }

    // * Routes
    // GET /api/order-addresses - list all order addresses
    @GetMapping
    public List<OrderAddressDto> list()
    {
        return repo.findAll().stream()
                .map(oa -> new OrderAddressDto(
                        oa.getId().toString(),
                        oa.getOrder().getId().toString(),
                        oa.getStreet(),
                        oa.getCity(),
                        oa.getState(),
                        oa.getPostalCode(),
                        oa.getCountry()))
                .collect(Collectors.toList());
    }

    // GET /api/order-addresses/{id} - get order address by ID
    @GetMapping("/{id}")
    public ResponseEntity<OrderAddressDto> getById(@PathVariable String id)
    {
        try {
            UUID addressId = UUID.fromString(id);
            Optional<OrderAddress> orderAddress = repo.findById(addressId);
            
            if (orderAddress.isPresent()) {
                OrderAddress oa = orderAddress.get();
                OrderAddressDto dto = new OrderAddressDto(
                        oa.getId().toString(),
                        oa.getOrder().getId().toString(),
                        oa.getStreet(),
                        oa.getCity(),
                        oa.getState(),
                        oa.getPostalCode(),
                        oa.getCountry());
                return ResponseEntity.ok(dto);
            }
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // GET /api/order-addresses/order/{orderId} - get address for an order
    @GetMapping("/order/{orderId}")
    public ResponseEntity<OrderAddressDto> getByOrderId(@PathVariable String orderId)
    {
        try {
            UUID orderUuid = UUID.fromString(orderId);
            OrderAddress orderAddress = repo.findByOrderId(orderUuid);
            
            if (orderAddress != null) {
                OrderAddressDto dto = new OrderAddressDto(
                        orderAddress.getId().toString(),
                        orderAddress.getOrder().getId().toString(),
                        orderAddress.getStreet(),
                        orderAddress.getCity(),
                        orderAddress.getState(),
                        orderAddress.getPostalCode(),
                        orderAddress.getCountry());
                return ResponseEntity.ok(dto);
            }
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // GET /api/order-addresses/city/{city} - get addresses by city
    @GetMapping("/city/{city}")
    public List<OrderAddressDto> getByCity(@PathVariable String city)
    {
        return repo.findByCity(city).stream()
                .map(oa -> new OrderAddressDto(
                        oa.getId().toString(),
                        oa.getOrder().getId().toString(),
                        oa.getStreet(),
                        oa.getCity(),
                        oa.getState(),
                        oa.getPostalCode(),
                        oa.getCountry()))
                .collect(Collectors.toList());
    }

    // GET /api/order-addresses/country/{country} - get addresses by country
    @GetMapping("/country/{country}")
    public List<OrderAddressDto> getByCountry(@PathVariable String country)
    {
        return repo.findByCountry(country.toUpperCase()).stream()
                .map(oa -> new OrderAddressDto(
                        oa.getId().toString(),
                        oa.getOrder().getId().toString(),
                        oa.getStreet(),
                        oa.getCity(),
                        oa.getState(),
                        oa.getPostalCode(),
                        oa.getCountry()))
                .collect(Collectors.toList());
    }

    // * DTO
    // Represents an order address in API responses
    static class OrderAddressDto
    {
        // order address ID
        private final String id;
        // order ID
        private final String orderId;
        // street address
        private final String street;
        // city name
        private final String city;
        // state/region/province
        private final String state;
        // postal/zip code
        private final String postalCode;
        // country code
        private final String country;

        // * Constructor
        public OrderAddressDto(String id, String orderId, String street, String city,
                              String state, String postalCode, String country)
        {
            this.id = id;
            this.orderId = orderId;
            this.street = street;
            this.city = city;
            this.state = state;
            this.postalCode = postalCode;
            this.country = country;
        }

        // * Getters (for serialization)
        // order address ID
        public String getId()
        {
            return id;
        }

        // order ID
        public String getOrderId()
        {
            return orderId;
        }

        // street
        public String getStreet()
        {
            return street;
        }

        // city
        public String getCity()
        {
            return city;
        }

        // state
        public String getState()
        {
            return state;
        }

        // postal code
        public String getPostalCode()
        {
            return postalCode;
        }

        // country
        public String getCountry()
        {
            return country;
        }
    }
}