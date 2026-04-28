package com.SupplementDistributor.SupplementDistributor.service;

import com.SupplementDistributor.SupplementDistributor.dto.request.StockMovementRequestDTO;
import com.SupplementDistributor.SupplementDistributor.dto.response.StockMovementResponseDTO;

import java.util.List;

public interface IStockService {
    StockMovementResponseDTO stockIn(StockMovementRequestDTO request);
    StockMovementResponseDTO stockOut(StockMovementRequestDTO request);
    List<StockMovementResponseDTO> getStockHistory();
}
