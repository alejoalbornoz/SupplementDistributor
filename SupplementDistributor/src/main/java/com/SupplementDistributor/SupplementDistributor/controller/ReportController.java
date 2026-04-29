package com.SupplementDistributor.SupplementDistributor.controller;

import com.SupplementDistributor.SupplementDistributor.dto.response.ProductResponseDTO;
import com.SupplementDistributor.SupplementDistributor.service.IReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ReportController {
    private final IReportService reportService;

    @GetMapping("/top-products")
    public ResponseEntity<List<Map<String, Object>>> getTopSellingProducts() {
        return ResponseEntity.ok(reportService.getTopSellingProducts());
    }

    @GetMapping("/billing")
    public ResponseEntity<Map<String, Object>> getBillingByPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(reportService.getBillingByPeriod(start, end));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<ProductResponseDTO>> getLowStockProducts(
            @RequestParam(defaultValue = "10") Integer threshold) {
        return ResponseEntity.ok(reportService.getLowStockProducts(threshold));
    }
}
