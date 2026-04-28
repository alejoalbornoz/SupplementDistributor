package com.SupplementDistributor.SupplementDistributor.service;

import com.SupplementDistributor.SupplementDistributor.dto.response.CategoryResponseDTO;
import com.SupplementDistributor.SupplementDistributor.model.Category;

import java.util.List;

public interface ICategoryService {
    List<CategoryResponseDTO> getAllCategories();
    CategoryResponseDTO getCategoryById(Long id);
    CategoryResponseDTO createCategory(String name, String description);
    Category getCategoryEntityById(Long id);
}
