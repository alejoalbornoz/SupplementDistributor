package com.SupplementDistributor.SupplementDistributor.mapper;

import com.SupplementDistributor.SupplementDistributor.dto.response.*;
import com.SupplementDistributor.SupplementDistributor.model.*;

public class Mapper {


    public static UserResponseDTO toDTO(User u) {
        if (u == null) return null;
        return UserResponseDTO.builder()
                .id(u.getId())
                .firstName(u.getFirstName())
                .lastName(u.getLastName())
                .email(u.getEmail())
                .phone(u.getPhone())
                .role(u.getRole())
                .build();
    }



    public static CategoryResponseDTO toDTO(Category c) {
        if (c == null) return null;
        return CategoryResponseDTO.builder()
                .id(c.getId())
                .name(c.getName())
                .description(c.getDescription())
                .build();
    }



    public static ProductResponseDTO toDTO(Product p) {
        if (p == null) return null;
        return ProductResponseDTO.builder()
                .id(p.getId())
                .name(p.getName())
                .brand(p.getBrand())
                .description(p.getDescription())
                .price(p.getPrice())
                .stock(p.getStock())
                .category(toDTO(p.getCategory()))
                .build();
    }



    public static StockMovementResponseDTO toDTO(StockMovement s) {
        if (s == null) return null;
        return StockMovementResponseDTO.builder()
                .id(s.getId())
                .type(s.getType())
                .quantity(s.getQuantity())
                .reason(s.getReason())
                .createdAt(s.getCreatedAt())
                .productName(s.getProduct() != null ? s.getProduct().getName() : null)
                .build();
    }



    public static OrderItemResponseDTO toDTO(OrderItem oi) {
        if (oi == null) return null;
        return OrderItemResponseDTO.builder()
                .id(oi.getId())
                .quantity(oi.getQuantity())
                .unitPrice(oi.getUnitPrice())
                .subtotal(oi.getSubtotal())
                .productName(oi.getProduct() != null ? oi.getProduct().getName() : null)
                .productBrand(oi.getProduct() != null ? oi.getProduct().getBrand() : null)
                .build();
    }



    public static ShipmentResponseDTO toDTO(Shipment s) {
        if (s == null) return null;
        return ShipmentResponseDTO.builder()
                .id(s.getId())
                .address(s.getAddress())
                .trackingCode(s.getTrackingCode())
                .status(s.getStatus())
                .shippedAt(s.getShippedAt())
                .deliveredAt(s.getDeliveredAt())
                .build();
    }



    public static OrderResponseDTO toDTO(Order o) {
        if (o == null) return null;
        return OrderResponseDTO.builder()
                .id(o.getId())
                .status(o.getStatus())
                .total(o.getTotal())
                .notes(o.getNotes())
                .createdAt(o.getCreatedAt())
                .user(toDTO(o.getUser()))
                .items(o.getItems()
                        .stream()
                        .map(Mapper::toDTO)
                        .toList())
                .shipment(toDTO(o.getShipment()))
                .build();
    }
}
