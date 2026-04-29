package com.SupplementDistributor.SupplementDistributor.controller;

import com.SupplementDistributor.SupplementDistributor.dto.request.CreateShipmentRequestDTO;
import com.SupplementDistributor.SupplementDistributor.dto.response.ShipmentResponseDTO;
import com.SupplementDistributor.SupplementDistributor.enums.ShipmentStatus;
import com.SupplementDistributor.SupplementDistributor.service.IShipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shipments")
@RequiredArgsConstructor
public class ShipmentController {
    private final IShipmentService shipmentService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ShipmentResponseDTO> createShipment(
            @Valid @RequestBody CreateShipmentRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(shipmentService.createShipment(request));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ShipmentResponseDTO> getShipmentByOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(shipmentService.getShipmentByOrder(orderId));
    }

    @GetMapping("/tracking/{trackingCode}")
    public ResponseEntity<ShipmentResponseDTO> getShipmentByTrackingCode(
            @PathVariable String trackingCode) {
        return ResponseEntity.ok(shipmentService.getShipmentByTrackingCode(trackingCode));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ShipmentResponseDTO> updateShipmentStatus(
            @PathVariable Long id,
            @RequestParam ShipmentStatus status) {
        return ResponseEntity.ok(shipmentService.updateShipmentStatus(id, status));
    }
}
