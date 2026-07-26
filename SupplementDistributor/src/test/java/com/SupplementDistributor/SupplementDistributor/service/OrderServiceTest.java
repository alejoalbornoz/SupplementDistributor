package com.SupplementDistributor.SupplementDistributor.service;

import com.SupplementDistributor.SupplementDistributor.dto.request.CreateOrderRequestDTO;
import com.SupplementDistributor.SupplementDistributor.dto.request.UpdateOrderStatusRequestDTO;
import com.SupplementDistributor.SupplementDistributor.dto.response.OrderResponseDTO;
import com.SupplementDistributor.SupplementDistributor.dto.response.PageResponseDTO;
import com.SupplementDistributor.SupplementDistributor.enums.OrderStatus;
import com.SupplementDistributor.SupplementDistributor.enums.RoleName;
import com.SupplementDistributor.SupplementDistributor.exception.InsufficientStockException;
import com.SupplementDistributor.SupplementDistributor.exception.ResourceNotFoundException;
import com.SupplementDistributor.SupplementDistributor.model.*;
import com.SupplementDistributor.SupplementDistributor.repository.IOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private IOrderRepository orderRepository;

    @Mock
    private UserService userService;

    @Mock
    private IProductService productService;

    @InjectMocks
    private OrderService orderService;

    private User user;
    private Product product;
    private Order order;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .firstName("Juan")
                .lastName("Pérez")
                .email("juan@gmail.com")
                .password("hashedpassword")
                .phone("1234567890")
                .role(RoleName.CLIENT)
                .active(true)
                .build();

        Category category = Category.builder()
                .id(1L)
                .name("Proteína")
                .build();

        product = Product.builder()
                .id(1L)
                .name("Whey Protein")
                .brand("Optimum Nutrition")
                .price(new BigDecimal("5000.00"))
                .stock(50)
                .active(true)
                .category(category)
                .build();

        order = Order.builder()
                .id(1L)
                .user(user)
                .status(OrderStatus.PENDING)
                .total(new BigDecimal("10000.00"))
                .notes("Entregar en la tarde")
                .items(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // ─────────────────────────────────────────
    // createOrder
    // ─────────────────────────────────────────

    @Test
    void createOrder_shouldCalculateTotalCorrectly() {
        // Arrange
        CreateOrderRequestDTO.OrderItemRequest itemRequest =
                new CreateOrderRequestDTO.OrderItemRequest();
        itemRequest.setProductId(1L);
        itemRequest.setQuantity(2);

        CreateOrderRequestDTO request = new CreateOrderRequestDTO();
        request.setItems(List.of(itemRequest));
        request.setNotes("Entregar en la tarde");

        when(userService.findEntityById(1L)).thenReturn(user);
        when(productService.findActiveById(1L)).thenReturn(product);
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
            Order o = inv.getArgument(0);
            o.setId(1L);
            o.setStatus(OrderStatus.PENDING);  // ← simula el @PrePersist
            o.setCreatedAt(LocalDateTime.now());
            return o;
        });

        // Act
        OrderResponseDTO result = orderService.createOrder(1L, request);

        // Assert
        assertThat(result.getTotal()).isEqualByComparingTo(new BigDecimal("10000.00"));
        assertThat(result.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(result.getItems()).hasSize(1);
    }

    @Test
    void createOrder_shouldDecreaseProductStock() {
        CreateOrderRequestDTO.OrderItemRequest itemRequest =
                new CreateOrderRequestDTO.OrderItemRequest();
        itemRequest.setProductId(1L);
        itemRequest.setQuantity(5);

        CreateOrderRequestDTO request = new CreateOrderRequestDTO();
        request.setItems(List.of(itemRequest));

        when(userService.findEntityById(1L)).thenReturn(user);
        when(productService.findActiveById(1L)).thenReturn(product);
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
            Order o = inv.getArgument(0);
            o.setId(1L);
            o.setStatus(OrderStatus.PENDING);  // ← simula el @PrePersist
            o.setCreatedAt(LocalDateTime.now());
            return o;
        });

        orderService.createOrder(1L, request);

        assertThat(product.getStock()).isEqualTo(45);
    }

    @Test
    void createOrder_withInsufficientStock_shouldThrowException() {
        // Arrange
        CreateOrderRequestDTO.OrderItemRequest itemRequest =
                new CreateOrderRequestDTO.OrderItemRequest();
        itemRequest.setProductId(1L);
        itemRequest.setQuantity(100); // más que el stock disponible (50)

        CreateOrderRequestDTO request = new CreateOrderRequestDTO();
        request.setItems(List.of(itemRequest));

        when(userService.findEntityById(1L)).thenReturn(user);
        when(productService.findActiveById(1L)).thenReturn(product);

        // Act & Assert
        assertThatThrownBy(() -> orderService.createOrder(1L, request))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessageContaining("Whey Protein");

        verify(orderRepository, never()).save(any());
    }

    @Test
    void createOrder_withMultipleItems_shouldCalculateTotalCorrectly() {
        Product product2 = Product.builder()
                .id(2L)
                .name("Creatina")
                .brand("Universal")
                .price(new BigDecimal("3000.00"))
                .stock(30)
                .active(true)
                .category(Category.builder().id(1L).name("Creatina").build())
                .build();

        CreateOrderRequestDTO.OrderItemRequest item1 =
                new CreateOrderRequestDTO.OrderItemRequest();
        item1.setProductId(1L);
        item1.setQuantity(2);

        CreateOrderRequestDTO.OrderItemRequest item2 =
                new CreateOrderRequestDTO.OrderItemRequest();
        item2.setProductId(2L);
        item2.setQuantity(3);

        CreateOrderRequestDTO request = new CreateOrderRequestDTO();
        request.setItems(List.of(item1, item2));

        when(userService.findEntityById(1L)).thenReturn(user);
        when(productService.findActiveById(1L)).thenReturn(product);
        when(productService.findActiveById(2L)).thenReturn(product2);
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
            Order o = inv.getArgument(0);
            o.setId(1L);
            o.setStatus(OrderStatus.PENDING);  // ← simula el @PrePersist
            o.setCreatedAt(LocalDateTime.now());
            return o;
        });

        OrderResponseDTO result = orderService.createOrder(1L, request);

        assertThat(result.getTotal()).isEqualByComparingTo(new BigDecimal("19000.00"));
        assertThat(result.getItems()).hasSize(2);
    }

    @Test
    void createOrder_whenUserNotFound_shouldThrowResourceNotFoundException() {
        // Arrange
        when(userService.findEntityById(99L))
                .thenThrow(new ResourceNotFoundException("User not found with id: 99"));

        CreateOrderRequestDTO request = new CreateOrderRequestDTO();
        request.setItems(List.of());

        // Act & Assert
        assertThatThrownBy(() -> orderService.createOrder(99L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(orderRepository, never()).save(any());
    }

    // ─────────────────────────────────────────
    // getAllOrders
    // ─────────────────────────────────────────

    @Test
    void getAllOrders_shouldReturnPagedOrders() {
        // Arrange
        Page<Order> page = new PageImpl<>(List.of(order));
        when(orderRepository.findAll(any(Pageable.class))).thenReturn(page);

        // Act
        PageResponseDTO<OrderResponseDTO> result = orderService.getAllOrders(0, 10);

        // Assert
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getStatus()).isEqualTo(OrderStatus.PENDING);
    }

    @Test
    void getAllOrders_whenEmpty_shouldReturnEmptyPage() {
        // Arrange
        Page<Order> emptyPage = new PageImpl<>(List.of());
        when(orderRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

        // Act
        PageResponseDTO<OrderResponseDTO> result = orderService.getAllOrders(0, 10);

        // Assert
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }

    // ─────────────────────────────────────────
    // getOrdersByUser
    // ─────────────────────────────────────────

    @Test
    void getOrdersByUser_shouldReturnOnlyUserOrders() {
        // Arrange
        Page<Order> page = new PageImpl<>(List.of(order));
        when(orderRepository.findByUserId(eq(1L), any(Pageable.class))).thenReturn(page);

        // Act
        PageResponseDTO<OrderResponseDTO> result = orderService.getOrdersByUser(1L, 0, 10);

        // Assert
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUser().getEmail())
                .isEqualTo("juan@gmail.com");
    }

    // ─────────────────────────────────────────
    // updateOrderStatus
    // ─────────────────────────────────────────

    @Test
    void updateOrderStatus_shouldChangeStatus() {
        // Arrange
        UpdateOrderStatusRequestDTO request = new UpdateOrderStatusRequestDTO();
        request.setStatus(OrderStatus.SHIPPED);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        OrderResponseDTO result = orderService.updateOrderStatus(1L, request);

        // Assert
        assertThat(result.getStatus()).isEqualTo(OrderStatus.SHIPPED);
    }

    @Test
    void updateOrderStatus_whenOrderNotFound_shouldThrowResourceNotFoundException() {
        // Arrange
        UpdateOrderStatusRequestDTO request = new UpdateOrderStatusRequestDTO();
        request.setStatus(OrderStatus.SHIPPED);

        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> orderService.updateOrderStatus(99L, request))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(orderRepository, never()).save(any());
    }

    @Test
    void updateOrderStatus_toCancelled_shouldWork() {
        // Arrange
        UpdateOrderStatusRequestDTO request = new UpdateOrderStatusRequestDTO();
        request.setStatus(OrderStatus.CANCELLED);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        OrderResponseDTO result = orderService.updateOrderStatus(1L, request);

        // Assert
        assertThat(result.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }
}