package com.SupplementDistributor.SupplementDistributor.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCategoryRequestDTO {
    @NotBlank(message = "Name is required")
    private String name;

    private String description;
}
