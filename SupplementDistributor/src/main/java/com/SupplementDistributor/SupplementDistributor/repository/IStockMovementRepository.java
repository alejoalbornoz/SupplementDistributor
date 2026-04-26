package com.SupplementDistributor.SupplementDistributor.repository;

import com.SupplementDistributor.SupplementDistributor.enums.MovementType;
import com.SupplementDistributor.SupplementDistributor.model.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IStockMovementRepository extends JpaRepository<StockMovement, Long> {

    List<StockMovement> findByProductId(Long productId);

    List<StockMovement> findByType(MovementType type);
}
