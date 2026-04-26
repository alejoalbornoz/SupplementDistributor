package com.SupplementDistributor.SupplementDistributor.dto.request;

import com.SupplementDistributor.SupplementDistributor.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateOrderStatusRequestDTO {
    @NotNull(message = "Status is required")
    private OrderStatus status;
}
