package com.example.xml_project.repository;

import com.example.xml_project.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository; // @Repository


@Repository

public interface UserRepository extends JpaRepository<User, Long> {
    // findAll() , findbyId() ... are generated auto by extending JpaRepository 
    // The methods are generated auto by Hibernate
}