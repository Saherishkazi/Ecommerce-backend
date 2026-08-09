package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.OrderResponse;
import com.ecommerce.backend.dto.OrderStatusUpdateRequest;
import com.ecommerce.backend.entity.OrderStatus;
import com.ecommerce.backend.exception.BadRequestException;
import com.ecommerce.backend.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final OrderService orderService;

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @PutMapping("/orders/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(@PathVariable Long id, @Valid @RequestBody OrderStatusUpdateRequest request) {
        OrderStatus status;
        try {
            status = OrderStatus.valueOf(request.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid status: " + request.getStatus());
        }
        return ResponseEntity.ok(orderService.updateStatus(id, status));
    }
}
