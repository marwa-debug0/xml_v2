package com.example.xml_project.repository;

import com.example.xml_project.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // CRUD methods are generated automatically by Spring Data JPA
}
