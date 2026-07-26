package com.SupplementDistributor.SupplementDistributor.controller;

import com.SupplementDistributor.SupplementDistributor.dto.request.CreateProductRequestDTO;
import com.SupplementDistributor.SupplementDistributor.dto.response.CategoryResponseDTO;
import com.SupplementDistributor.SupplementDistributor.dto.response.PageResponseDTO;
import com.SupplementDistributor.SupplementDistributor.dto.response.ProductResponseDTO;
import com.SupplementDistributor.SupplementDistributor.exception.GlobalExceptionHandler;
import com.SupplementDistributor.SupplementDistributor.exception.ResourceNotFoundException;
import com.SupplementDistributor.SupplementDistributor.security.JwtService;
import com.SupplementDistributor.SupplementDistributor.security.SecurityConfig;
import com.SupplementDistributor.SupplementDistributor.security.TokenBlacklistService;
import com.SupplementDistributor.SupplementDistributor.security.UserDetailsServiceImpl;
import com.SupplementDistributor.SupplementDistributor.service.IProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IProductService productService;

    // Dependencias de Security que WebMvcTest necesita
    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private TokenBlacklistService tokenBlacklistService;

    private ProductResponseDTO productResponse;
    private PageResponseDTO<ProductResponseDTO> pageResponse;

    @BeforeEach
    void setUp() {
        CategoryResponseDTO category = CategoryResponseDTO.builder()
                .id(1L)
                .name("Proteína")
                .description("Suplementos proteicos")
                .build();

        productResponse = ProductResponseDTO.builder()
                .id(1L)
                .name("Whey Protein")
                .brand("Optimum Nutrition")
                .description("100% Whey Gold Standard")
                .price(new BigDecimal("5000.00"))
                .stock(50)
                .category(category)
                .build();

        pageResponse = PageResponseDTO.<ProductResponseDTO>builder()
                .content(List.of(productResponse))
                .currentPage(0)
                .pageSize(10)
                .totalElements(1)
                .totalPages(1)
                .isFirst(true)
                .isLast(true)
                .build();
    }

    // ─────────────────────────────────────────
    // GET /api/products
    // ─────────────────────────────────────────

    @Test
    void getAllProducts_shouldReturn200WithPagedProducts() throws Exception {
        // Arrange
        when(productService.getAllProducts(anyInt(), anyInt(), anyString()))
                .thenReturn(pageResponse);

        // Act & Assert
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("Whey Protein"))
                .andExpect(jsonPath("$.content[0].price").value(5000.00))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.currentPage").value(0));
    }

    @Test
    void getAllProducts_withPaginationParams_shouldReturn200() throws Exception {
        // Arrange
        when(productService.getAllProducts(1, 5, "brand")).thenReturn(pageResponse);

        // Act & Assert
        mockMvc.perform(get("/api/products")
                        .param("page", "1")
                        .param("size", "5")
                        .param("sortBy", "brand"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllProducts_isPublic_shouldNotRequireAuth() throws Exception {
        // Arrange
        when(productService.getAllProducts(anyInt(), anyInt(), anyString()))
                .thenReturn(pageResponse);

        // Act & Assert — sin token y sin @WithMockUser
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk());
    }

    // ─────────────────────────────────────────
    // GET /api/products/{id}
    // ─────────────────────────────────────────

    @Test
    void getProductById_whenExists_shouldReturn200() throws Exception {
        // Arrange
        when(productService.getProductById(1L)).thenReturn(productResponse);

        // Act & Assert
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Whey Protein"))
                .andExpect(jsonPath("$.brand").value("Optimum Nutrition"));
    }

    @Test
    void getProductById_whenNotExists_shouldReturn404() throws Exception {
        // Arrange
        when(productService.getProductById(99L))
                .thenThrow(new ResourceNotFoundException("Product not found with id: 99"));

        // Act & Assert
        mockMvc.perform(get("/api/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found with id: 99"));
    }

    // ─────────────────────────────────────────
    // POST /api/products
    // ─────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    void createProduct_withAdminRole_shouldReturn201() throws Exception {
        // Arrange
        CreateProductRequestDTO request = new CreateProductRequestDTO();
        request.setName("Whey Protein");
        request.setBrand("Optimum Nutrition");
        request.setDescription("100% Whey Gold Standard");
        request.setPrice(new BigDecimal("5000.00"));
        request.setStock(50);
        request.setCategoryId(1L);

        when(productService.createProduct(any(CreateProductRequestDTO.class)))
                .thenReturn(productResponse);

        // Act & Assert
        mockMvc.perform(post("/api/products")
                        .with(csrf())  // ← agregá esto
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    void createProduct_withClientRole_shouldReturn403() throws Exception {
        // Arrange
        CreateProductRequestDTO request = new CreateProductRequestDTO();
        request.setName("Whey Protein");
        request.setBrand("Optimum Nutrition");
        request.setPrice(new BigDecimal("5000.00"));
        request.setStock(50);
        request.setCategoryId(1L);

        // Act & Assert — cliente no puede crear productos
        mockMvc.perform(post("/api/products")
                        .with(csrf())  // ← agregá esto
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createProduct_withoutAuth_shouldReturn403() throws Exception {
        // Arrange
        CreateProductRequestDTO request = new CreateProductRequestDTO();
        request.setName("Whey Protein");
        request.setBrand("Optimum Nutrition");
        request.setPrice(new BigDecimal("5000.00"));
        request.setStock(50);
        request.setCategoryId(1L);

        // Act & Assert — sin autenticación
        mockMvc.perform(post("/api/products")
                        .with(csrf())  // ← agregá esto
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createProduct_withInvalidBody_shouldReturn400() throws Exception {
        // Arrange — body vacío, falta name, brand, price, stock
        CreateProductRequestDTO request = new CreateProductRequestDTO();

        // Act & Assert
        mockMvc.perform(post("/api/products")
                        .with(csrf())  // ← agregá esto
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ─────────────────────────────────────────
    // DELETE /api/products/{id}
    // ─────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteProduct_withAdminRole_shouldReturn204() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/products/1")
                        .with(csrf()))  // ← agregá esto
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    void deleteProduct_withClientRole_shouldReturn403() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/products/1")
                        .with(csrf()))  // ← agregá esto
                .andExpect(status().isForbidden());
    }
}