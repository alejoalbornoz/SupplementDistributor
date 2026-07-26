package com.SupplementDistributor.SupplementDistributor.service;

import com.SupplementDistributor.SupplementDistributor.dto.request.CreateProductRequestDTO;
import com.SupplementDistributor.SupplementDistributor.dto.request.UpdateProductRequestDTO;
import com.SupplementDistributor.SupplementDistributor.dto.response.PageResponseDTO;
import com.SupplementDistributor.SupplementDistributor.dto.response.ProductResponseDTO;
import com.SupplementDistributor.SupplementDistributor.exception.ResourceNotFoundException;
import com.SupplementDistributor.SupplementDistributor.model.Category;
import com.SupplementDistributor.SupplementDistributor.model.Product;
import com.SupplementDistributor.SupplementDistributor.repository.IProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private IProductRepository productRepository;

    @Mock
    private ICategoryService categoryService;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private Category category;

    @BeforeEach
    void setUp() {
        category = Category.builder()
                .id(1L)
                .name("Proteína")
                .description("Suplementos proteicos")
                .build();

        product = Product.builder()
                .id(1L)
                .name("Whey Protein")
                .brand("Optimum Nutrition")
                .description("100% Whey Gold Standard")
                .price(new BigDecimal("5000.00"))
                .stock(50)
                .active(true)
                .category(category)
                .build();
    }

    // ─────────────────────────────────────────
    // getAllProducts
    // ─────────────────────────────────────────

    @Test
    void getAllProducts_shouldReturnPagedProducts() {
        // Arrange
        Page<Product> productPage = new PageImpl<>(List.of(product));
        when(productRepository.findByActiveTrue(any(Pageable.class))).thenReturn(productPage);

        // Act
        PageResponseDTO<ProductResponseDTO> result = productService.getAllProducts(0, 10, "name");

        // Assert
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Whey Protein");
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void getAllProducts_whenEmpty_shouldReturnEmptyPage() {
        // Arrange
        Page<Product> emptyPage = new PageImpl<>(List.of());
        when(productRepository.findByActiveTrue(any(Pageable.class))).thenReturn(emptyPage);

        // Act
        PageResponseDTO<ProductResponseDTO> result = productService.getAllProducts(0, 10, "name");

        // Assert
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }

    // ─────────────────────────────────────────
    // getProductById
    // ─────────────────────────────────────────

    @Test
    void getProductById_whenExists_shouldReturnProduct() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act
        ProductResponseDTO result = productService.getProductById(1L);

        // Assert
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Whey Protein");
        assertThat(result.getBrand()).isEqualTo("Optimum Nutrition");
        assertThat(result.getPrice()).isEqualByComparingTo(new BigDecimal("5000.00"));
    }

    @Test
    void getProductById_whenNotExists_shouldThrowResourceNotFoundException() {
        // Arrange
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> productService.getProductById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void getProductById_whenInactive_shouldThrowResourceNotFoundException() {
        // Arrange
        product.setActive(false);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act & Assert
        assertThatThrownBy(() -> productService.getProductById(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─────────────────────────────────────────
    // createProduct
    // ─────────────────────────────────────────

    @Test
    void createProduct_shouldSaveAndReturnProduct() {
        // Arrange
        CreateProductRequestDTO request = new CreateProductRequestDTO();
        request.setName("Whey Protein");
        request.setBrand("Optimum Nutrition");
        request.setDescription("100% Whey Gold Standard");
        request.setPrice(new BigDecimal("5000.00"));
        request.setStock(50);
        request.setCategoryId(1L);

        when(categoryService.getCategoryEntityById(1L)).thenReturn(category);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        ProductResponseDTO result = productService.createProduct(request);

        // Assert
        assertThat(result.getName()).isEqualTo("Whey Protein");
        assertThat(result.getPrice()).isEqualByComparingTo(new BigDecimal("5000.00"));
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void createProduct_whenCategoryNotFound_shouldThrowException() {
        // Arrange
        CreateProductRequestDTO request = new CreateProductRequestDTO();
        request.setCategoryId(99L);
        request.setName("Whey Protein");
        request.setPrice(new BigDecimal("5000.00"));
        request.setStock(50);

        when(categoryService.getCategoryEntityById(99L))
                .thenThrow(new ResourceNotFoundException("Category not found with id: 99"));

        // Act & Assert
        assertThatThrownBy(() -> productService.createProduct(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(productRepository, never()).save(any());
    }

    // ─────────────────────────────────────────
    // updateProduct
    // ─────────────────────────────────────────

    @Test
    void updateProduct_shouldUpdateOnlyProvidedFields() {
        // Arrange
        UpdateProductRequestDTO request = new UpdateProductRequestDTO();
        request.setPrice(new BigDecimal("6000.00"));

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        ProductResponseDTO result = productService.updateProduct(1L, request);

        // Assert — precio actualizado, nombre sin cambios
        assertThat(result.getPrice()).isEqualByComparingTo(new BigDecimal("6000.00"));
        assertThat(result.getName()).isEqualTo("Whey Protein");
    }

    @Test
    void updateProduct_whenNotExists_shouldThrowResourceNotFoundException() {
        // Arrange
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> productService.updateProduct(99L, new UpdateProductRequestDTO()))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(productRepository, never()).save(any());
    }

    // ─────────────────────────────────────────
    // deleteProduct (soft delete)
    // ─────────────────────────────────────────

    @Test
    void deleteProduct_shouldMarkAsInactive() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        productService.deleteProduct(1L);

        // Assert — verifica que se guardó con active = false
        verify(productRepository).save(argThat(p -> !p.getActive()));
    }

    @Test
    void deleteProduct_whenNotExists_shouldThrowResourceNotFoundException() {
        // Arrange
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> productService.deleteProduct(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(productRepository, never()).save(any());
    }
}