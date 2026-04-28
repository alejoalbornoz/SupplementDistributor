package com.SupplementDistributor.SupplementDistributor.service;

import com.SupplementDistributor.SupplementDistributor.dto.request.CreateProductRequestDTO;
import com.SupplementDistributor.SupplementDistributor.dto.request.UpdateProductRequestDTO;
import com.SupplementDistributor.SupplementDistributor.dto.response.ProductResponseDTO;
import com.SupplementDistributor.SupplementDistributor.model.Product;

import java.util.List;

public interface IProductService {
    List<ProductResponseDTO> getAllProducts();
    ProductResponseDTO getProductById(Long id);
    ProductResponseDTO createProduct(CreateProductRequestDTO request);
    ProductResponseDTO updateProduct(Long id, UpdateProductRequestDTO request);
    void deleteProduct(Long id);
    Product findActiveById(Long id);
}
