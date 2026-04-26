package com.SupplementDistributor.SupplementDistributor.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateShipmentRequestDTO {
    @NotNull(message = "Order is required")
    private Long orderId;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Tracking code is required")
    private String trackingCode;
}
