package com.SupplementDistributor.SupplementDistributor.repository;

import com.SupplementDistributor.SupplementDistributor.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IOrderItemRepository extends JpaRepository <OrderItem, Long>{
    List<OrderItem> findByOrderId(Long orderId);

    // Para el reporte de productos más vendidos
    @Query("""
        SELECT oi.product.id, oi.product.name, SUM(oi.quantity) as totalSold
        FROM OrderItem oi
        GROUP BY oi.product.id, oi.product.name
        ORDER BY totalSold DESC
    """)
    List<Object[]> findTopSellingProducts();
}
