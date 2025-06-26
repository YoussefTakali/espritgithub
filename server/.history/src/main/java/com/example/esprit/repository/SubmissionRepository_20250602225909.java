package com.example.esprit.repository;

import com.example.esprit.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    
    boolean existsByTaskIdAndStudentId(Long taskId, String studentId);
    
    boolean existsByTaskIdAndGroupId(Long taskId, Long groupId);
    
    Optional<Submission> findByTaskIdAndStudentId(Long taskId, String studentId);
    
    Optional<Submission> findByTaskIdAndGroupId(Long taskId, Long groupId);
    <
}
