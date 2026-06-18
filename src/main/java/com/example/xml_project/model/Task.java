package com.example.xml_project.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

// @Entity : maps this class to a table in MySQL
@Entity
@Table(name = "tasks")
@Data // Lombok : getters, setters, toString, equals
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement(localName = "task") // Root tag in the XML representation
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le titre de la tâche est obligatoire")
    private String title;

    private boolean completed;
}
