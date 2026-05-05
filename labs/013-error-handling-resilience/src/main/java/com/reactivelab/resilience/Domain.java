package com.reactivelab.resilience;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class Item {
    private String id;
    private String name;
    private Double price;
}

class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String id) {
        super("Product with ID " + id + " not found");
    }
}

class ServiceUnavailableException extends RuntimeException {
    public ServiceUnavailableException() {
        super("Downstream service is currently unavailable");
    }
}
