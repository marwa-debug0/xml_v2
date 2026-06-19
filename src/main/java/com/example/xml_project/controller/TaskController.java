package com.example.xml_project.controller;

import com.example.xml_project.model.Task;
import com.example.xml_project.service.DtdValidatorService;
import com.example.xml_project.service.TaskService;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.xml.sax.SAXException;

import java.util.List;

/**
 * Contrôleur REST pour la ressource Task.
 *
 * Routes disponibles :
 *   GET    /api/tasks          — liste toutes les tâches
 *   GET    /api/tasks/{id}     — une tâche par id
 *   POST   /api/tasks          — crée une tâche (JSON)
 *   PUT    /api/tasks/{id}     — met à jour une tâche
 *   DELETE /api/tasks/{id}     — supprime une tâche
 *   POST   /api/tasks/xml      — crée une tâche depuis XML validé par DTD
 */
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final DtdValidatorService dtdValidatorService;

    // GET /api/tasks
    @GetMapping(produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE
    })
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    // GET /api/tasks/{id}
    @GetMapping(value = "/{id}", produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE
    })
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/tasks (JSON)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE
    })
    public ResponseEntity<Task> createTask(@Valid @RequestBody Task task) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.createTask(task));
    }

    // PUT /api/tasks/{id}
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Task> updateTask(@PathVariable Long id,
                                           @Valid @RequestBody Task updated) {
        return taskService.updateTask(id, updated)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/tasks/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        return taskService.deleteTask(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    // ─────────────────────────────────────────────────────────────────
    // POST /api/tasks/xml — crée une tâche depuis XML validé par DTD
    //
    // Exemple de corps :
    //   <?xml version="1.0" encoding="UTF-8"?>
    //   <!DOCTYPE task SYSTEM "task.dtd">
    //   <task>
    //     <title>Rédiger le rapport</title>
    //     <completed>false</completed>
    //   </task>
    // ─────────────────────────────────────────────────────────────────
    @PostMapping(
            value = "/xml",
            consumes = MediaType.APPLICATION_XML_VALUE,
            produces = MediaType.APPLICATION_XML_VALUE
    )
    public ResponseEntity<?> createTaskFromXml(@RequestBody String xmlBody) {
        try {
            dtdValidatorService.validate(xmlBody);
            XmlMapper xmlMapper = new XmlMapper();
            Task task = xmlMapper.readValue(xmlBody, Task.class);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(taskService.createTask(task));
        } catch (SAXException e) {
            return ResponseEntity.badRequest()
                    .body("XML invalide (DTD) : " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Erreur interne : " + e.getMessage());
        }
    }
}
