package com.SupplementDistributor.SupplementDistributor.config;

import com.SupplementDistributor.SupplementDistributor.enums.RoleName;
import com.SupplementDistributor.SupplementDistributor.model.Category;
import com.SupplementDistributor.SupplementDistributor.model.Product;
import com.SupplementDistributor.SupplementDistributor.model.User;
import com.SupplementDistributor.SupplementDistributor.repository.ICategoryRepository;
import com.SupplementDistributor.SupplementDistributor.repository.IProductRepository;
import com.SupplementDistributor.SupplementDistributor.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final IUserRepository userRepository;
    private final ICategoryRepository categoryRepository;
    private final IProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        seedUsers();
        seedCategories();
        seedProducts();
    }

    // ─────────────────────────────────────────
    // USERS
    // ─────────────────────────────────────────

    private void seedUsers() {
        if (userRepository.count() > 0) {
            log.info("Users already seeded — skipping");
            return;
        }

        User admin = User.builder()
                .firstName("Admin")
                .lastName("User")
                .email("admin@supplements.com")
                .password(passwordEncoder.encode("admin123"))
                .phone("1134567890")
                .role(RoleName.ADMIN)
                .build();

        User client = User.builder()
                .firstName("Juan")
                .lastName("Pérez")
                .email("juan@gmail.com")
                .password(passwordEncoder.encode("client123"))
                .phone("1187654321")
                .role(RoleName.CLIENT)
                .build();

        userRepository.saveAll(List.of(admin, client));
        log.info("✅ Users seeded — admin@supplements.com / admin123");
    }

    // ─────────────────────────────────────────
    // CATEGORIES
    // ─────────────────────────────────────────

    private void seedCategories() {
        if (categoryRepository.count() > 0) {
            log.info("Categories already seeded — skipping");
            return;
        }

        List<Category> categories = List.of(
                Category.builder().name("Proteína").description("Suplementos proteicos para recuperación muscular").build(),
                Category.builder().name("Creatina").description("Mejora la fuerza y el rendimiento explosivo").build(),
                Category.builder().name("Pre-workout").description("Energía y concentración antes del entrenamiento").build(),
                Category.builder().name("BCAA").description("Aminoácidos de cadena ramificada para recuperación").build()
        );

        categoryRepository.saveAll(categories);
        log.info("✅ Categories seeded — 4 categories created");
    }

    // ─────────────────────────────────────────
    // PRODUCTS
    // ─────────────────────────────────────────

    private void seedProducts() {
        if (productRepository.count() > 0) {
            log.info("Products already seeded — skipping");
            return;
        }

        Category proteina = categoryRepository.findByName("Proteína").orElseThrow();
        Category creatina = categoryRepository.findByName("Creatina").orElseThrow();
        Category preWorkout = categoryRepository.findByName("Pre-workout").orElseThrow();
        Category bcaa = categoryRepository.findByName("BCAA").orElseThrow();

        List<Product> products = List.of(
                Product.builder()
                        .name("Whey Gold Standard")
                        .brand("Optimum Nutrition")
                        .description("100% Whey Protein, la proteína más vendida del mundo. 24g de proteína por porción.")
                        .price(new BigDecimal("8500.00"))
                        .stock(100)
                        .category(proteina)
                        .build(),
                Product.builder()
                        .name("Iso Whey Zero")
                        .brand("BioTechUSA")
                        .description("Proteína isolada sin lactosa, sin gluten. Ideal para intolerantes.")
                        .price(new BigDecimal("9200.00"))
                        .stock(60)
                        .category(proteina)
                        .build(),
                Product.builder()
                        .name("Creatina Monohidrato")
                        .brand("Universal Nutrition")
                        .description("Creatina monohidrato pura. Aumenta la fuerza y el volumen muscular.")
                        .price(new BigDecimal("4500.00"))
                        .stock(80)
                        .category(creatina)
                        .build(),
                Product.builder()
                        .name("Creatine HCL")
                        .brand("MuscleTech")
                        .description("Creatina HCL de alta absorción. No produce retención de líquidos.")
                        .price(new BigDecimal("5800.00"))
                        .stock(45)
                        .category(creatina)
                        .build(),
                Product.builder()
                        .name("C4 Original")
                        .brand("Cellucor")
                        .description("Pre-workout explosivo con 150mg de cafeína, beta-alanina y creatina.")
                        .price(new BigDecimal("6500.00"))
                        .stock(55)
                        .category(preWorkout)
                        .build(),
                Product.builder()
                        .name("BCAA 2:1:1")
                        .brand("Scitec Nutrition")
                        .description("Aminoácidos esenciales en proporción óptima 2:1:1. Reduce el catabolismo muscular.")
                        .price(new BigDecimal("3800.00"))
                        .stock(70)
                        .category(bcaa)
                        .build()
        );

        productRepository.saveAll(products);
        log.info("✅ Products seeded — 6 products created");
    }
}