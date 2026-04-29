package com.SupplementDistributor.SupplementDistributor.controller;


import com.SupplementDistributor.SupplementDistributor.dto.response.CategoryResponseDTO;
import com.SupplementDistributor.SupplementDistributor.service.ICategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {
    private final ICategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponseDTO> createCategory(
            @RequestParam String name,
            @RequestParam(required = false) String description) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryService.createCategory(name, description));
    }
}
