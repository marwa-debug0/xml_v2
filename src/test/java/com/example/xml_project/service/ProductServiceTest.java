package com.example.xml_project.service;

import com.example.xml_project.model.Product;
import com.example.xml_project.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires de la couche service ProductService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires — ProductService")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product laptop;

    @BeforeEach
    void setUp() {
        laptop = new Product(1L, "Ordinateur", 1200.0, "Laptop haute performance");
    }

    @Test
    @DisplayName("getAllProducts — doit retourner la liste complète")
    void getAllProducts_shouldReturnList() {
        when(productRepository.findAll()).thenReturn(List.of(laptop));

        List<Product> result = productService.getAllProducts();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Ordinateur");
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getProductById — doit retourner le produit s'il existe")
    void getProductById_shouldReturnProduct_whenExists() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(laptop));

        Optional<Product> result = productService.getProductById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getPrice()).isEqualTo(1200.0);
    }

    @Test
    @DisplayName("getProductById — doit retourner Optional vide si introuvable")
    void getProductById_shouldReturnEmpty_whenNotExists() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Product> result = productService.getProductById(99L);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("createProduct — doit sauvegarder et retourner le produit")
    void createProduct_shouldSaveAndReturn() {
        when(productRepository.save(laptop)).thenReturn(laptop);

        Product result = productService.createProduct(laptop);

        assertThat(result.getName()).isEqualTo("Ordinateur");
        verify(productRepository, times(1)).save(laptop);
    }

    @Test
    @DisplayName("updateProduct — doit modifier le produit s'il existe")
    void updateProduct_shouldUpdateFields_whenExists() {
        Product updated = new Product(null, "Clavier", 50.0, "Clavier mécanique");
        when(productRepository.findById(1L)).thenReturn(Optional.of(laptop));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        Optional<Product> result = productService.updateProduct(1L, updated);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Clavier");
        assertThat(result.get().getPrice()).isEqualTo(50.0);
    }

    @Test
    @DisplayName("deleteProduct — doit retourner true et supprimer le produit")
    void deleteProduct_shouldReturnTrue_whenExists() {
        when(productRepository.existsById(1L)).thenReturn(true);

        boolean result = productService.deleteProduct(1L);

        assertThat(result).isTrue();
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("deleteProduct — doit retourner false si produit introuvable")
    void deleteProduct_shouldReturnFalse_whenNotExists() {
        when(productRepository.existsById(99L)).thenReturn(false);

        boolean result = productService.deleteProduct(99L);

        assertThat(result).isFalse();
        verify(productRepository, never()).deleteById(any());
    }
}
