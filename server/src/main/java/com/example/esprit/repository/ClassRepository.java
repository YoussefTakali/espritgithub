package com.example.esprit.repository;

import com.example.esprit.model.Class;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ClassRepository extends JpaRepository<Class, Long> {
    Set<Class> findByTeacherAssignments_TeacherId(String teacherId);
}