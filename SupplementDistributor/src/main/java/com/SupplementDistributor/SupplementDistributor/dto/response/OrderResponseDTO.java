package com.SupplementDistributor.SupplementDistributor.dto.response;


import com.SupplementDistributor.SupplementDistributor.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {
    private Long id;
    private OrderStatus status;
    private BigDecimal total;
    private String notes;
    private LocalDateTime createdAt;
    private UserResponseDTO user;
    private List<OrderItemResponseDTO> items;
    private ShipmentResponseDTO shipment;
}
