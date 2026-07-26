package com.SupplementDistributor.SupplementDistributor.service;

import com.SupplementDistributor.SupplementDistributor.dto.request.CreateOrderRequestDTO;
import com.SupplementDistributor.SupplementDistributor.dto.request.UpdateOrderStatusRequestDTO;
import com.SupplementDistributor.SupplementDistributor.dto.response.OrderResponseDTO;
import com.SupplementDistributor.SupplementDistributor.dto.response.PageResponseDTO;

import java.util.List;

public interface IOrderService {
    PageResponseDTO<OrderResponseDTO> getAllOrders(int page, int size);
    PageResponseDTO<OrderResponseDTO> getOrdersByUser(Long userId, int page, int size);
    OrderResponseDTO createOrder(Long userId, CreateOrderRequestDTO request);
    OrderResponseDTO updateOrderStatus(Long id, UpdateOrderStatusRequestDTO request);
}
