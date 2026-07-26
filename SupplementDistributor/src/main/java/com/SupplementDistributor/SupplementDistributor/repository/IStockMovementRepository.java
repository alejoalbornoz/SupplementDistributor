package com.SupplementDistributor.SupplementDistributor.repository;

import com.SupplementDistributor.SupplementDistributor.enums.MovementType;
import com.SupplementDistributor.SupplementDistributor.model.StockMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IStockMovementRepository extends JpaRepository<StockMovement, Long> {

    Page<StockMovement> findAll(Pageable pageable);

    List<StockMovement> findByProductId(Long productId);

    List<StockMovement> findByType(MovementType type);
}
