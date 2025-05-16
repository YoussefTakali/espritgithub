package com.example.esprit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.esprit.model.ProjectStudentEnrollment;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectStudentEnrollmentRepository extends JpaRepository<ProjectStudentEnrollment, Long> {
    List<ProjectStudentEnrollment> findByStudentId(String studentId);
    List<ProjectStudentEnrollment> findByProjectId(Long projectId);
    Optional<ProjectStudentEnrollment> findByStudentIdAndProjectId(String studentId, Long projectId);
    void deleteByStudentIdAndProjectId(String studentId, Long projectId);
}