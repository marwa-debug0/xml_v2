package com.example.xml_project.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

// @Entity : maps this class to a table in MySQL
@Entity
@Table(name = "products")
@Data // Lombok : getters, setters, toString, equals
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement(localName = "product") // Root tag in the XML representation
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom du produit est obligatoire")
    private String name;

    @PositiveOrZero(message = "Le prix doit être positif ou nul")
    private double price;
}
