package com.example.xml_project.controller;

import com.example.xml_project.model.Product;
import com.example.xml_project.service.DtdValidatorService;
import com.example.xml_project.service.ProductService;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.xml.sax.SAXException;

import java.util.List;

/**
 * Contrôleur REST pour la ressource Product.
 *
 * Routes disponibles :
 * GET /api/products — liste tous les produits
 * GET /api/products/{id} — un produit par id
 * POST /api/products — crée un produit (JSON)
 * PUT /api/products/{id} — met à jour un produit
 * DELETE /api/products/{id} — supprime un produit
 * POST /api/products/xml — crée un produit depuis XML validé par DTD
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

        private final ProductService productService;
        private final DtdValidatorService dtdValidatorService;

        // ─────────────────────────────────────────────
        // GET /api/products — retourne JSON ou XML selon Accept header
        // ─────────────────────────────────────────────
        @GetMapping(produces = {
                        MediaType.APPLICATION_JSON_VALUE,
                        MediaType.APPLICATION_XML_VALUE
        })
        public List<Product> getAllProducts() {
                return productService.getAllProducts();
        }

        // ─────────────────────────────────────────────
        // GET /api/products/{id}
        // ─────────────────────────────────────────────
        @GetMapping(value = "/{id}", produces = {
                        MediaType.APPLICATION_JSON_VALUE,
                        MediaType.APPLICATION_XML_VALUE
        })
        public ResponseEntity<Product> getProductById(@PathVariable Long id) {
                return productService.getProductById(id)
                                .map(ResponseEntity::ok) // 200 OK si trouvé
                                .orElse(ResponseEntity.notFound().build()); // 404 si absent
        }

        // ─────────────────────────────────────────────
        // POST /api/products — crée un produit via JSON
        // ─────────────────────────────────────────────
        @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = {
                        MediaType.APPLICATION_JSON_VALUE,
                        MediaType.APPLICATION_XML_VALUE
        })
        public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) {
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(productService.createProduct(product)); // 201 Created
        }

        // ─────────────────────────────────────────────
        // PUT /api/products/{id} — mise à jour
        // ─────────────────────────────────────────────
        @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<Product> updateProduct(@PathVariable Long id,
                        @Valid @RequestBody Product updated) {
                return productService.updateProduct(id, updated)
                                .map(ResponseEntity::ok)
                                .orElse(ResponseEntity.notFound().build());
        }

        // ─────────────────────────────────────────────
        // DELETE /api/products/{id}
        // ─────────────────────────────────────────────
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
                return productService.deleteProduct(id)
                                ? ResponseEntity.noContent().build() // 204 No Content
                                : ResponseEntity.notFound().build(); // 404 Not Found
        }

        // ─────────────────────────────────────────────────────────────────
        // POST /api/products/xml — crée un produit depuis XML validé par DTD
        //
        // Le corps doit inclure une déclaration DOCTYPE, exemple :
        // <?xml version="1.0" encoding="UTF-8"?>
        // <!DOCTYPE product SYSTEM "product.dtd">
        // <product>
        // <name>Ordinateur</name>
        // <price>1200.00</price>
        // </product>
        // ─────────────────────────────────────────────────────────────────
        @PostMapping(value = "/xml", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
        public ResponseEntity<?> createProductFromXml(@RequestBody String xmlBody) {
                try {
                        // Étape 1 : Validation DTD — lance SAXException si invalide
                        dtdValidatorService.validate(xmlBody);

                        // Étape 2 : Désérialisation XML → objet Product
                        XmlMapper xmlMapper = new XmlMapper();
                        Product product = xmlMapper.readValue(xmlBody, Product.class);

                        // Étape 3 : Persistance et réponse
                        return ResponseEntity.status(HttpStatus.CREATED)
                                        .body(productService.createProduct(product));

                } catch (SAXException e) {
                        // XML ne respecte pas la DTD
                        return ResponseEntity.badRequest()
                                        .body("XML invalide (DTD) : " + e.getMessage());
                } catch (Exception e) {
                        return ResponseEntity.internalServerError()
                                        .body("Erreur interne : " + e.getMessage());
                }
        }
}
