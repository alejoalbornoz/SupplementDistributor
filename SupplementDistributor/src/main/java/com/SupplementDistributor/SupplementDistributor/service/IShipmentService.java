package com.SupplementDistributor.SupplementDistributor.service;

import com.SupplementDistributor.SupplementDistributor.dto.request.CreateShipmentRequestDTO;
import com.SupplementDistributor.SupplementDistributor.dto.response.ShipmentResponseDTO;
import com.SupplementDistributor.SupplementDistributor.enums.ShipmentStatus;

public interface IShipmentService {
    ShipmentResponseDTO createShipment(CreateShipmentRequestDTO request);
    ShipmentResponseDTO getShipmentByOrder(Long orderId);
    ShipmentResponseDTO getShipmentByTrackingCode(String trackingCode);
    ShipmentResponseDTO updateShipmentStatus(Long id, ShipmentStatus status);
}
