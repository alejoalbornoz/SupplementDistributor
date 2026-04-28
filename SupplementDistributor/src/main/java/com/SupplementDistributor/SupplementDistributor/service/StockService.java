package com.SupplementDistributor.SupplementDistributor.service;

import com.SupplementDistributor.SupplementDistributor.dto.request.StockMovementRequestDTO;
import com.SupplementDistributor.SupplementDistributor.dto.response.StockMovementResponseDTO;
import com.SupplementDistributor.SupplementDistributor.enums.MovementType;
import com.SupplementDistributor.SupplementDistributor.exception.InsufficientStockException;
import com.SupplementDistributor.SupplementDistributor.mapper.Mapper;
import com.SupplementDistributor.SupplementDistributor.model.Product;
import com.SupplementDistributor.SupplementDistributor.model.StockMovement;
import com.SupplementDistributor.SupplementDistributor.repository.IStockMovementRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService implements IStockService{
    private final IStockMovementRepository stockMovementRepository;
    private final IProductService productService;

    @Override
    @Transactional
    public StockMovementResponseDTO stockIn(StockMovementRequestDTO request) {
        Product product = productService.findActiveById(request.getProductId());
        product.setStock(product.getStock() + request.getQuantity());

        StockMovement movement = StockMovement.builder()
                .type(MovementType.IN)
                .quantity(request.getQuantity())
                .reason(request.getReason())
                .product(product)
                .build();

        return Mapper.toDTO(stockMovementRepository.save(movement));
    }

    @Override
    public StockMovementResponseDTO stockOut(StockMovementRequestDTO request) {
         Product product = productService.findActiveById(request.getProductId());

        if (product.getStock() < request.getQuantity()) {
            throw new InsufficientStockException(
                    "Insufficient stock for product: " + product.getName() +
                            ". Available: " + product.getStock() +
                            ", Requested: " + request.getQuantity()
            );
        }

        product.setStock(product.getStock() - request.getQuantity());

        StockMovement movement = StockMovement.builder()
                .type(MovementType.OUT)
                .quantity(request.getQuantity())
                .reason(request.getReason())
                .product(product)
                .build();

        return Mapper.toDTO(stockMovementRepository.save(movement));
    }

    @Override
    public List<StockMovementResponseDTO> getStockHistory() {
        return List.of();
    }
}
