package com.SupplementDistributor.SupplementDistributor.dto.response;



import com.SupplementDistributor.SupplementDistributor.enums.MovementType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockMovementResponseDTO {
    private Long id;
    private MovementType type;
    private Integer quantity;
    private String reason;
    private LocalDateTime createdAt;
    private String productName;

}
