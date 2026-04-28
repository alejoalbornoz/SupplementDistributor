package com.SupplementDistributor.SupplementDistributor.service;

import com.SupplementDistributor.SupplementDistributor.dto.request.CreateOrderRequestDTO;
import com.SupplementDistributor.SupplementDistributor.dto.request.UpdateOrderStatusRequestDTO;
import com.SupplementDistributor.SupplementDistributor.dto.response.OrderResponseDTO;

import com.SupplementDistributor.SupplementDistributor.exception.InsufficientStockException;
import com.SupplementDistributor.SupplementDistributor.exception.ResourceNotFoundException;
import com.SupplementDistributor.SupplementDistributor.mapper.Mapper;
import com.SupplementDistributor.SupplementDistributor.model.Order;
import com.SupplementDistributor.SupplementDistributor.model.OrderItem;
import com.SupplementDistributor.SupplementDistributor.model.Product;
import com.SupplementDistributor.SupplementDistributor.model.User;
import com.SupplementDistributor.SupplementDistributor.repository.IOrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;


@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService{

    private final IOrderRepository orderRepository;
    private final UserService userService;
    private final IProductService productService;

    @Override
    @Transactional
    public OrderResponseDTO createOrder(Long userId, CreateOrderRequestDTO request) {
        User user = userService.findEntityById(userId);

        Order order = Order.builder()
                .user(user)
                .notes(request.getNotes())
                .build();

        BigDecimal total = BigDecimal.ZERO;

        for (CreateOrderRequestDTO.OrderItemRequest itemRequest : request.getItems()) {
            Product product = productService.findActiveById(itemRequest.getProductId());

            if (product.getStock() < itemRequest.getQuantity()) {
                throw new InsufficientStockException(
                        "Insufficient stock for product: " + product.getName()
                );
            }

            BigDecimal subtotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

            OrderItem item = OrderItem.builder()
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(product.getPrice())
                    .subtotal(subtotal)
                    .order(order)
                    .build();

            order.getItems().add(item);
            total = total.add(subtotal);
            product.setStock(product.getStock() - itemRequest.getQuantity());
        }

        order.setTotal(total);
        return Mapper.toDTO(orderRepository.save(order));
    }

    @Override
    public List<OrderResponseDTO> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(Mapper::toDTO)
                .toList();
    }

    @Override
    public List<OrderResponseDTO> getOrdersByUser(Long userId) {
        return orderRepository.findByUserId(userId)
                .stream()
                .map(Mapper::toDTO)
                .toList();
    }

    @Override
    public OrderResponseDTO updateOrderStatus(Long id, UpdateOrderStatusRequestDTO request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        order.setStatus(request.getStatus());
        return Mapper.toDTO(orderRepository.save(order));
    }
}
