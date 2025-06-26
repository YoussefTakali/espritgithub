package com.example.esprit.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.esprit.model.AssignmentScope;
import com.example.esprit.model.TaskAssignment;

public interface TaskAssignmentRepository extends JpaRepository<TaskAssignment, Long> {
    
    // Check if a specific student is already assigned
    boolean existsByTaskIdAndStudentId(Long taskId, String studentId);
    
    List<TaskAssignment> findByTaskId(Long taskId);
    
    // Find all individual assignments for a task
    List<TaskAssignment> findByTaskIdAndScope(Long taskId, AssignmentScope scope);
    
    long countByTaskId(Long taskId);
    
    // Bulk assignment check
    @Query("SELECT ta.studentId FROM TaskAssignment ta WHERE ta.task.id = :taskId AND ta.scope = 'INDIVIDUAL'")
    Set<String> findAssignedStudentIds(@Param("taskId") Long taskId);
    
    // Find TaskAssignment by taskId and groupId
    Optional<TaskAssignment> findByTaskIdAndGroupId(Long taskId, Long groupId);
    
    // Find TaskAssignment by taskId and studentId
    Optional<TaskAssignment> findByTaskIdAndStudentId(Long taskId, String studentId);
    void deleteByGroupId(Long groupId);

}
