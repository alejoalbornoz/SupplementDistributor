package com.SupplementDistributor.SupplementDistributor.repository;

import com.SupplementDistributor.SupplementDistributor.model.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IShipmentRepository extends JpaRepository<Shipment, Long> {
    Optional<Shipment> findByOrderId(Long orderId);

    Optional<Shipment> findByTrackingCode(String trackingCode);
}
