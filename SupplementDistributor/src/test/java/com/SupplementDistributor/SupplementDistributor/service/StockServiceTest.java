package com.SupplementDistributor.SupplementDistributor.service;

import com.SupplementDistributor.SupplementDistributor.dto.request.StockMovementRequestDTO;
import com.SupplementDistributor.SupplementDistributor.dto.response.StockMovementResponseDTO;
import com.SupplementDistributor.SupplementDistributor.enums.MovementType;
import com.SupplementDistributor.SupplementDistributor.exception.InsufficientStockException;
import com.SupplementDistributor.SupplementDistributor.model.Category;
import com.SupplementDistributor.SupplementDistributor.model.Product;
import com.SupplementDistributor.SupplementDistributor.model.StockMovement;
import com.SupplementDistributor.SupplementDistributor.repository.IStockMovementRepository;
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
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private IStockMovementRepository stockMovementRepository;

    @Mock
    private IProductService productService;

    @InjectMocks
    private StockService stockService;

    private Product product;
    private StockMovementRequestDTO request;

    @BeforeEach
    void setUp() {
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

        request = new StockMovementRequestDTO();
        request.setProductId(1L);
        request.setQuantity(10);
        request.setReason("Compra a proveedor");
    }

    // ─────────────────────────────────────────
    // stockIn
    // ─────────────────────────────────────────

    @Test
    void stockIn_shouldIncreaseProductStock() {
        // Arrange
        when(productService.findActiveById(1L)).thenReturn(product);
        when(stockMovementRepository.save(any(StockMovement.class)))
                .thenAnswer(inv -> {
                    StockMovement m = inv.getArgument(0);
                    m.setId(1L);
                    m.setCreatedAt(LocalDateTime.now());
                    return m;
                });

        // Act
        StockMovementResponseDTO result = stockService.stockIn(request);

        // Assert
        assertThat(product.getStock()).isEqualTo(60); // 50 + 10
        assertThat(result.getType()).isEqualTo(MovementType.IN);
        assertThat(result.getQuantity()).isEqualTo(10);
        assertThat(result.getProductName()).isEqualTo("Whey Protein");
    }

    @Test
    void stockIn_shouldSaveMovementWithCorrectType() {
        // Arrange
        when(productService.findActiveById(1L)).thenReturn(product);
        when(stockMovementRepository.save(any())).thenAnswer(inv -> {
            StockMovement m = inv.getArgument(0);
            m.setId(1L);
            m.setCreatedAt(LocalDateTime.now());
            return m;
        });

        // Act
        stockService.stockIn(request);

        // Assert — verifica que el movimiento guardado tiene tipo IN
        verify(stockMovementRepository).save(argThat(m ->
                m.getType() == MovementType.IN &&
                        m.getQuantity() == 10 &&
                        m.getReason().equals("Compra a proveedor")
        ));
    }

    @Test
    void stockIn_withLargeQuantity_shouldIncreaseStockCorrectly() {
        // Arrange
        request.setQuantity(1000);
        when(productService.findActiveById(1L)).thenReturn(product);
        when(stockMovementRepository.save(any())).thenAnswer(inv -> {
            StockMovement m = inv.getArgument(0);
            m.setId(1L);
            m.setCreatedAt(LocalDateTime.now());
            return m;
        });

        // Act
        stockService.stockIn(request);

        // Assert
        assertThat(product.getStock()).isEqualTo(1050); // 50 + 1000
    }

    // ─────────────────────────────────────────
    // stockOut
    // ─────────────────────────────────────────

    @Test
    void stockOut_withSufficientStock_shouldDecreaseProductStock() {
        // Arrange
        when(productService.findActiveById(1L)).thenReturn(product);
        when(stockMovementRepository.save(any(StockMovement.class)))
                .thenAnswer(inv -> {
                    StockMovement m = inv.getArgument(0);
                    m.setId(1L);
                    m.setCreatedAt(LocalDateTime.now());
                    return m;
                });

        // Act
        StockMovementResponseDTO result = stockService.stockOut(request);

        // Assert
        assertThat(product.getStock()).isEqualTo(40); // 50 - 10
        assertThat(result.getType()).isEqualTo(MovementType.OUT);
        assertThat(result.getQuantity()).isEqualTo(10);
    }

    @Test
    void stockOut_withInsufficientStock_shouldThrowInsufficientStockException() {
        // Arrange
        request.setQuantity(100); // más que el stock disponible (50)
        when(productService.findActiveById(1L)).thenReturn(product);

        // Act & Assert
        assertThatThrownBy(() -> stockService.stockOut(request))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessageContaining("Whey Protein");

        // Verifica que el stock no se modificó
        assertThat(product.getStock()).isEqualTo(50);
        verify(stockMovementRepository, never()).save(any());
    }

    @Test
    void stockOut_withExactStock_shouldLeaveStockAtZero() {
        // Arrange
        request.setQuantity(50); // exactamente el stock disponible
        when(productService.findActiveById(1L)).thenReturn(product);
        when(stockMovementRepository.save(any())).thenAnswer(inv -> {
            StockMovement m = inv.getArgument(0);
            m.setId(1L);
            m.setCreatedAt(LocalDateTime.now());
            return m;
        });

        // Act
        stockService.stockOut(request);

        // Assert
        assertThat(product.getStock()).isZero();
    }

    @Test
    void stockOut_withStockOfOne_andQuantityOfTwo_shouldThrowException() {
        // Arrange
        product.setStock(1);
        request.setQuantity(2);
        when(productService.findActiveById(1L)).thenReturn(product);

        // Act & Assert
        assertThatThrownBy(() -> stockService.stockOut(request))
                .isInstanceOf(InsufficientStockException.class);

        assertThat(product.getStock()).isEqualTo(1); // stock sin cambios
        verify(stockMovementRepository, never()).save(any());
    }

    // ─────────────────────────────────────────
    // getStockHistory
    // ─────────────────────────────────────────

    @Test
    void getStockHistory_shouldReturnPagedMovements() {
        // Arrange
        StockMovement movement = StockMovement.builder()
                .id(1L)
                .type(MovementType.IN)
                .quantity(10)
                .reason("Compra a proveedor")
                .product(product)
                .createdAt(LocalDateTime.now())
                .build();

        Page<StockMovement> page = new PageImpl<>(List.of(movement));
        when(stockMovementRepository.findAll(any(Pageable.class))).thenReturn(page);

        // Act
        var result = stockService.getStockHistory(0, 10);

        // Assert
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getType()).isEqualTo(MovementType.IN);
        assertThat(result.getContent().get(0).getProductName()).isEqualTo("Whey Protein");
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void getStockHistory_whenEmpty_shouldReturnEmptyPage() {
        // Arrange
        Page<StockMovement> emptyPage = new PageImpl<>(List.of());
        when(stockMovementRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

        // Act
        var result = stockService.getStockHistory(0, 10);

        // Assert
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }
}