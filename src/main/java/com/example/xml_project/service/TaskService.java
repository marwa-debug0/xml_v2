package com.example.xml_project.service;

import com.example.xml_project.model.Task;
import com.example.xml_project.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Couche métier pour les tâches.
 */
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    /** Retourne toutes les tâches */
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    /** Retourne une tâche par son id */
    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    /** Crée et sauvegarde une nouvelle tâche */
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    /** Met à jour une tâche existante */
    public Optional<Task> updateTask(Long id, Task updated) {
        return taskRepository.findById(id).map(existing -> {
            existing.setTitle(updated.getTitle());
            existing.setCompleted(updated.isCompleted());
            existing.setDescription(updated.getDescription());
            return taskRepository.save(existing);
        });
    }

    /** Supprime une tâche — retourne false si introuvable */
    public boolean deleteTask(Long id) {
        if (!taskRepository.existsById(id)) return false;
        taskRepository.deleteById(id);
        return true;
    }
}
