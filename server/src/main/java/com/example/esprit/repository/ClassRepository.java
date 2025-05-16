package com.example.esprit.repository;

import com.example.esprit.model.Class;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassRepository extends JpaRepository<Class, Long> {
    List<Class> findByTeacherAssignments_TeacherId(String teacherId);
}