package com.example.xml_project.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement; // Defines the root tag when converting objects to xml
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

// @Entity : is a table in MySql
@Entity
@Table(name = "users")
@Data // Lombok : generates getters, setters, toString, equals
@NoArgsConstructor // Lombok : generates an empty constructors
@AllArgsConstructor // Lombok : generates a full constructor
@JacksonXmlRootElement(localName = "user") // Root tag in the xml file

public class User {

    // Primary key
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Automatically generate the id
    private Long id;

    // Name
    @NotBlank(message = "Le nom est obligatoire")
    private String name;

    // Email
    @Email(message = "Email invalide")
    @NotBlank(message = "L'email est obligatoire")
    private String email;

    // Password 
    @NotBlank(message = "Le mot de passe est obligatoire")
    private String password;

}

// A user should have : id + name + email + password 