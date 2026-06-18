package com.example.xml_project.repository;

import com.example.xml_project.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    // CRUD methods are generated automatically by Spring Data JPA
}
