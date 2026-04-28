package com.SupplementDistributor.SupplementDistributor.service;

import com.SupplementDistributor.SupplementDistributor.dto.request.CreateProductRequestDTO;
import com.SupplementDistributor.SupplementDistributor.dto.request.UpdateProductRequestDTO;
import com.SupplementDistributor.SupplementDistributor.dto.response.ProductResponseDTO;
import com.SupplementDistributor.SupplementDistributor.exception.ResourceNotFoundException;
import com.SupplementDistributor.SupplementDistributor.mapper.Mapper;
import com.SupplementDistributor.SupplementDistributor.model.Category;
import com.SupplementDistributor.SupplementDistributor.model.Product;
import com.SupplementDistributor.SupplementDistributor.repository.IProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService{
    private final IProductRepository productRepository;
    private final ICategoryService categoryService;

    @Override
    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findByActiveTrue()
                .stream()
                .map(Mapper::toDTO)
                .toList();
    }

    @Override
    public ProductResponseDTO getProductById(Long id) {
        return Mapper.toDTO(findActiveById(id));
    }

    @Override
    public ProductResponseDTO createProduct(CreateProductRequestDTO request) {
        Category category = categoryService.getCategoryEntityById(request.getCategoryId());

        Product product = Product.builder()
                .name(request.getName())
                .brand(request.getBrand())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .category(category)
                .build();


        return Mapper.toDTO(productRepository.save(product));
    }

    @Override
    public ProductResponseDTO updateProduct(Long id, UpdateProductRequestDTO request) {

        Product product = findActiveById(id);

        if (request.getName() != null) product.setName(request.getName());
        if (request.getBrand() != null) product.setBrand(request.getBrand());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getPrice() != null) product.setPrice(request.getPrice());
        if (request.getStock() != null) product.setStock(request.getStock());
        if (request.getCategoryId() != null) {
            Category category = categoryService.getCategoryEntityById(request.getCategoryId());
            product.setCategory(category);
        }

        return Mapper.toDTO(productRepository.save(product));
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = findActiveById(id);
        product.setActive(false);
        productRepository.save(product);
    }


    @Override
    public Product findActiveById(Long id) {
        return productRepository.findById(id)
                .filter(Product::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }
}
