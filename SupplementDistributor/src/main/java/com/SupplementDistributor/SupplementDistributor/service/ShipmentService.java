package com.SupplementDistributor.SupplementDistributor.service;


import com.SupplementDistributor.SupplementDistributor.dto.request.CreateShipmentRequestDTO;
import com.SupplementDistributor.SupplementDistributor.dto.response.ShipmentResponseDTO;
import com.SupplementDistributor.SupplementDistributor.enums.ShipmentStatus;
import com.SupplementDistributor.SupplementDistributor.exception.ResourceNotFoundException;
import com.SupplementDistributor.SupplementDistributor.mapper.Mapper;
import com.SupplementDistributor.SupplementDistributor.model.Order;
import com.SupplementDistributor.SupplementDistributor.model.Shipment;
import com.SupplementDistributor.SupplementDistributor.repository.IOrderRepository;
import com.SupplementDistributor.SupplementDistributor.repository.IShipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ShipmentService implements IShipmentService{


    private final IShipmentRepository shipmentRepository;
    private final IOrderRepository orderRepository;


    @Override
    public ShipmentResponseDTO createShipment(CreateShipmentRequestDTO request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + request.getOrderId()));

        Shipment shipment = Shipment.builder()
                .order(order)
                .address(request.getAddress())
                .trackingCode(request.getTrackingCode())
                .build();

        return Mapper.toDTO(shipmentRepository.save(shipment));
    }


    @Override
    public ShipmentResponseDTO getShipmentByOrder(Long orderId) {
        Shipment shipment = shipmentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found for order: " + orderId));
        return Mapper.toDTO(shipment);
    }

    @Override
    public ShipmentResponseDTO getShipmentByTrackingCode(String trackingCode) {
        Shipment shipment = shipmentRepository.findByTrackingCode(trackingCode)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with tracking: " + trackingCode));
        return Mapper.toDTO(shipment);
    }

    @Override
    public ShipmentResponseDTO updateShipmentStatus(Long id, ShipmentStatus status) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found with id: " + id));

        shipment.setStatus(status);

        if (status == ShipmentStatus.IN_TRANSIT) shipment.setShippedAt(LocalDateTime.now());
        if (status == ShipmentStatus.DELIVERED) shipment.setDeliveredAt(LocalDateTime.now());

        return Mapper.toDTO(shipmentRepository.save(shipment));
    }
}
