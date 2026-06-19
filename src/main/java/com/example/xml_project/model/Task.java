package com.example.xml_project.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Entité JPA représentant une tâche.
 * Sérialisable en JSON et XML (root tag : <task>).
 */
@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement(localName = "task")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le titre de la tâche est obligatoire")
    private String title;

    // false = en cours, true = terminée
    private boolean completed;

    // Description optionnelle
    private String description;
}
