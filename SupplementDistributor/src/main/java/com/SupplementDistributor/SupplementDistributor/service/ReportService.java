package com.SupplementDistributor.SupplementDistributor.service;


import com.SupplementDistributor.SupplementDistributor.dto.response.ProductResponseDTO;
import com.SupplementDistributor.SupplementDistributor.mapper.Mapper;
import com.SupplementDistributor.SupplementDistributor.model.Order;
import com.SupplementDistributor.SupplementDistributor.repository.IOrderItemRepository;
import com.SupplementDistributor.SupplementDistributor.repository.IOrderRepository;
import com.SupplementDistributor.SupplementDistributor.repository.IProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportService implements IReportService{
    private final IOrderRepository orderRepository;
    private final IOrderItemRepository orderItemRepository;
    private final IProductRepository productRepository;

    @Override
    public List<Map<String, Object>> getTopSellingProducts() {
        return orderItemRepository.findTopSellingProducts()
                .stream()
                .map(row -> Map.of(
                        "productId", row[0],
                        "productName", row[1],
                        "totalSold", row[2]
                ))
                .toList();
    }

    @Override
    public Map<String, Object> getBillingByPeriod(LocalDateTime start, LocalDateTime end) {
        List<Order> orders = orderRepository.findByCreatedAtBetween(start, end);

        BigDecimal total = orders.stream()
                .map(Order::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return Map.of(
                "from", start,
                "to", end,
                "totalOrders", orders.size(),
                "totalBilling", total
        );
    }

    @Override
    public List<ProductResponseDTO> getLowStockProducts(Integer threshold) {
        return productRepository.findByStockLessThan(threshold)
                .stream()
                .map(Mapper::toDTO)
                .toList();
    }
}
