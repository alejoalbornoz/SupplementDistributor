package com.SupplementDistributor.SupplementDistributor.service;

import com.SupplementDistributor.SupplementDistributor.dto.request.CreateOrderRequestDTO;
import com.SupplementDistributor.SupplementDistributor.dto.request.UpdateOrderStatusRequestDTO;
import com.SupplementDistributor.SupplementDistributor.dto.response.OrderResponseDTO;

import java.util.List;

public interface IOrderService {
    OrderResponseDTO createOrder(Long userId, CreateOrderRequestDTO request);
    List<OrderResponseDTO> getAllOrders();
    List<OrderResponseDTO> getOrdersByUser(Long userId);
    OrderResponseDTO updateOrderStatus(Long id, UpdateOrderStatusRequestDTO request);
}
