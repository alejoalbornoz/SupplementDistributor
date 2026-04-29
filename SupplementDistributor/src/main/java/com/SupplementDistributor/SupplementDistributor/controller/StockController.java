package com.SupplementDistributor.SupplementDistributor.controller;

import com.SupplementDistributor.SupplementDistributor.dto.request.StockMovementRequestDTO;
import com.SupplementDistributor.SupplementDistributor.dto.response.StockMovementResponseDTO;
import com.SupplementDistributor.SupplementDistributor.service.IStockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class StockController {
    private final IStockService stockService;

    @PostMapping("/in")
    public ResponseEntity<StockMovementResponseDTO> stockIn(
            @Valid @RequestBody StockMovementRequestDTO request) {
        return ResponseEntity.ok(stockService.stockIn(request));
    }

    @PostMapping("/out")
    public ResponseEntity<StockMovementResponseDTO> stockOut(
            @Valid @RequestBody StockMovementRequestDTO request) {
        return ResponseEntity.ok(stockService.stockOut(request));
    }

    @GetMapping("/history")
    public ResponseEntity<List<StockMovementResponseDTO>> getStockHistory() {
        return ResponseEntity.ok(stockService.getStockHistory());
    }
}
