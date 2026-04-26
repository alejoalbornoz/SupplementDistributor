package com.SupplementDistributor.SupplementDistributor.dto.request;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequestDTO {
    @NotEmpty(message = "Order must have at least one item")
    private List<OrderItemRequest> items;

    private String notes;

    @Data
    public static class OrderItemRequest {

        @NotNull(message = "Product is required")
        private Long productId;

        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be greater than 0")
        private Integer quantity;
    }
}
