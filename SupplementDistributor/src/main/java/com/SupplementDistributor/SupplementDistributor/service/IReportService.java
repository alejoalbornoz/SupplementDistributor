package com.SupplementDistributor.SupplementDistributor.service;

import com.SupplementDistributor.SupplementDistributor.dto.response.ProductResponseDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface IReportService {
    List<Map<String, Object>> getTopSellingProducts();
    Map<String, Object> getBillingByPeriod(LocalDateTime start, LocalDateTime end);
    List<ProductResponseDTO> getLowStockProducts(Integer threshold);
}
