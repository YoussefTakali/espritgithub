package com.example.esprit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.esprit.model.Project;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByCreatedBy(String teacherId);
    List<Project> findByAssociatedClassId(Long classId);
    List<Project> findByStudentEnrollments_StudentId(String studentId);
}