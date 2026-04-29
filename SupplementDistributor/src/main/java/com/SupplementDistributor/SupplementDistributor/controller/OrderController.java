package com.SupplementDistributor.SupplementDistributor.controller;

import com.SupplementDistributor.SupplementDistributor.dto.request.CreateOrderRequestDTO;
import com.SupplementDistributor.SupplementDistributor.dto.request.UpdateOrderStatusRequestDTO;
import com.SupplementDistributor.SupplementDistributor.dto.response.OrderResponseDTO;
import com.SupplementDistributor.SupplementDistributor.service.IOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final IOrderService orderService;

    // ADMIN ve todas las órdenes, CLIENT solo las suyas
    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getOrders(
            @AuthenticationPrincipal UserDetails userDetails) {

        boolean isAdmin = userDetails.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return ResponseEntity.ok(orderService.getAllOrders());
        }

        // Extraemos el userId del UserDetails
        // Lo implementaremos en Security
        Long userId = ((com.SupplementDistributor.SupplementDistributor.model.User) userDetails).getId();
        return ResponseEntity.ok(orderService.getOrdersByUser(userId));
    }

    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<OrderResponseDTO> createOrder(
            @Valid @RequestBody CreateOrderRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = ((com.SupplementDistributor.SupplementDistributor.model.User) userDetails).getId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createOrder(userId, request));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequestDTO request) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, request));
    }
}
