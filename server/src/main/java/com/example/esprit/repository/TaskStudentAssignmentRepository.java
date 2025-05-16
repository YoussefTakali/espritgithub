package com.example.esprit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.esprit.model.TaskStudentAssignment;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskStudentAssignmentRepository extends JpaRepository<TaskStudentAssignment, Long> {
    List<TaskStudentAssignment> findByStudentId(String studentId);
    List<TaskStudentAssignment> findByTaskId(Long taskId);
    Optional<TaskStudentAssignment> findByStudentIdAndTaskId(String studentId, Long taskId);
    void deleteByStudentIdAndTaskId(String studentId, Long taskId);
}