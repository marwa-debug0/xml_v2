package com.example.xml_project.repository;

import com.example.xml_project.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository JPA pour Task.
 * Hérite de findAll(), findById(), save(), deleteById(), existsById()...
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
}
