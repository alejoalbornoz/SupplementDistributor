package com.SupplementDistributor.SupplementDistributor.repository;

import com.SupplementDistributor.SupplementDistributor.enums.OrderStatus;
import com.SupplementDistributor.SupplementDistributor.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IOrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserId(Long userId);

    List<Order> findByStatus(OrderStatus status);

    // Para el reporte de facturación por período
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :start AND :end")
    List<Order> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
