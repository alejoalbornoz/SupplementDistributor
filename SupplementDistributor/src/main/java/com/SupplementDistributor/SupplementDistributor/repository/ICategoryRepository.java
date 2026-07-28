package com.SupplementDistributor.SupplementDistributor.repository;

import com.SupplementDistributor.SupplementDistributor.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ICategoryRepository extends JpaRepository <Category, Long>{
    Boolean existsByName(String name);
    Optional<Category> findByName(String name);
}
