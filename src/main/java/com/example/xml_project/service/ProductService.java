package com.example.xml_project.service;

import com.example.xml_project.model.Product;
import com.example.xml_project.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Couche métier pour les produits.
 * Encapsule toute la logique entre le controller et le repository.
 */
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    /** Retourne tous les produits */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /** Retourne un produit par son id (Optional = vide si introuvable) */
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    /** Crée et sauvegarde un nouveau produit */
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    /** Met à jour un produit existant */
    public Optional<Product> updateProduct(Long id, Product updated) {
        return productRepository.findById(id).map(existing -> {
            existing.setName(updated.getName());
            existing.setPrice(updated.getPrice());
            existing.setDescription(updated.getDescription());
            return productRepository.save(existing);
        });
    }

    /** Supprime un produit — retourne false si introuvable */
    public boolean deleteProduct(Long id) {
        if (!productRepository.existsById(id)) return false;
        productRepository.deleteById(id);
        return true;
    }
}
