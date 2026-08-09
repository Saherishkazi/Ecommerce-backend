package com.ecommerce.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderStatusUpdateRequest {

    @NotBlank(message = "Status is required")
    private String status; // PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
}
