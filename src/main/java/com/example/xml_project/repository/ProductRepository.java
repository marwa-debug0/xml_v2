package com.example.xml_project.repository;

import com.example.xml_project.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository JPA pour Product.
 * Hérite de findAll(), findById(), save(), deleteById(), existsById()...
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
