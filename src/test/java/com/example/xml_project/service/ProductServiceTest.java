package com.example.xml_project.service;

import com.example.xml_project.model.Product;
import com.example.xml_project.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
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

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product(1L, "Keyboard", 49.99);
    }

    @Test
    void getAllProducts_shouldReturnList() {
        when(productRepository.findAll()).thenReturn(List.of(product));

        List<Product> result = productService.getAllProducts();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Keyboard");
    }

    @Test
    void getProductById_shouldReturnProduct_whenExists() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Optional<Product> result = productService.getProductById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getPrice()).isEqualTo(49.99);
    }

    @Test
    void getProductById_shouldReturnEmpty_whenNotExists() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThat(productService.getProductById(99L)).isEmpty();
    }

    @Test
    void createProduct_shouldSaveAndReturn() {
        when(productRepository.save(product)).thenReturn(product);

        Product result = productService.createProduct(product);

        assertThat(result.getName()).isEqualTo("Keyboard");
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void updateProduct_shouldUpdate_whenExists() {
        Product updated = new Product(null, "Mouse", 19.99);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        Optional<Product> result = productService.updateProduct(1L, updated);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Mouse");
        assertThat(result.get().getPrice()).isEqualTo(19.99);
    }

    @Test
    void updateProduct_shouldReturnEmpty_whenNotExists() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThat(productService.updateProduct(99L, product)).isEmpty();
        verify(productRepository, never()).save(any());
    }

    @Test
    void deleteProduct_shouldReturnTrue_whenExists() {
        when(productRepository.existsById(1L)).thenReturn(true);

        assertThat(productService.deleteProduct(1L)).isTrue();
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteProduct_shouldReturnFalse_whenNotExists() {
        when(productRepository.existsById(99L)).thenReturn(false);

        assertThat(productService.deleteProduct(99L)).isFalse();
        verify(productRepository, never()).deleteById(any());
    }
}
