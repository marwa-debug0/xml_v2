package com.example.xml_project.controller;

import com.example.xml_project.model.Product;
import com.example.xml_project.service.DtdValidatorService;
import com.example.xml_project.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.xml.sax.SAXException;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests d'intégration HTTP — ProductController.
 */
@WebMvcTest(ProductController.class)
@DisplayName("Tests HTTP — ProductController")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private DtdValidatorService dtdValidatorService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/products — doit retourner 200 avec la liste")
    void getAllProducts_shouldReturn200() throws Exception {
        Product laptop = new Product(1L, "Ordinateur", 1200.0, "Laptop");
        when(productService.getAllProducts()).thenReturn(List.of(laptop));

        mockMvc.perform(get("/api/products").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Ordinateur"))
                .andExpect(jsonPath("$[0].price").value(1200.0));
    }

    @Test
    @DisplayName("GET /api/products/{id} — doit retourner 200 si trouvé")
    void getProductById_shouldReturn200_whenFound() throws Exception {
        Product laptop = new Product(1L, "Ordinateur", 1200.0, "Laptop");
        when(productService.getProductById(1L)).thenReturn(Optional.of(laptop));

        mockMvc.perform(get("/api/products/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /api/products/{id} — doit retourner 404 si introuvable")
    void getProductById_shouldReturn404_whenNotFound() throws Exception {
        when(productService.getProductById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/products — doit retourner 201 Created")
    void createProduct_shouldReturn201() throws Exception {
        Product input = new Product(null, "Souris", 29.99, "Souris sans fil");
        Product saved = new Product(1L,   "Souris", 29.99, "Souris sans fil");
        when(productService.createProduct(any())).thenReturn(saved);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Souris"));
    }

    @Test
    @DisplayName("DELETE /api/products/{id} — doit retourner 204 si supprimé")
    void deleteProduct_shouldReturn204_whenFound() throws Exception {
        when(productService.deleteProduct(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/products/{id} — doit retourner 404 si introuvable")
    void deleteProduct_shouldReturn404_whenNotFound() throws Exception {
        when(productService.deleteProduct(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/products/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/products/xml — doit retourner 201 si XML valide selon DTD")
    void createProductFromXml_shouldReturn201_whenDtdValid() throws Exception {
        Product saved = new Product(1L, "Ordinateur", 1200.0, null);
        doNothing().when(dtdValidatorService).validate(any()); // DTD valide — pas d'exception
        when(productService.createProduct(any())).thenReturn(saved);

        String validXml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <!DOCTYPE product SYSTEM "product.dtd">
                <product>
                    <name>Ordinateur</name>
                    <price>1200.00</price>
                </product>
                """;

        mockMvc.perform(post("/api/products/xml")
                        .contentType(MediaType.APPLICATION_XML_VALUE)
                        .content(validXml))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /api/products/xml — doit retourner 400 si XML invalide selon DTD")
    void createProductFromXml_shouldReturn400_whenDtdInvalid() throws Exception {
        doThrow(new SAXException("Erreur DTD")).when(dtdValidatorService).validate(any());

        String invalidXml = """
                <?xml version="1.0" encoding="UTF-8"?>
                <!DOCTYPE product SYSTEM "product.dtd">
                <product>
                    <name>Ordinateur</name>
                </product>
                """;

        mockMvc.perform(post("/api/products/xml")
                        .contentType(MediaType.APPLICATION_XML_VALUE)
                        .content(invalidXml))
                .andExpect(status().isBadRequest());
    }
}
