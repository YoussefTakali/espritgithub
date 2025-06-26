package com.example.esprit.repository;

import com.example.esprit.model.Submission;
import com.example.esprit.model.SubmissionStatus;
import com.example.esprit.model.AssignmentScope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    
    List<Submission> findByTaskId(Long taskId);

    List<Submission> findBySubmittedBy(String studentId);

    List<Submission> findByGradedBy(String teacherId);

    List<Submission> findByTaskIdAndSubmittedBy(Long taskId, String studentId);

    List<Submission> findByStatus(SubmissionStatus status);

    Optional<Submission> findTopByTaskIdAndSubmittedByOrderBySubmissionDateDesc(Long taskId, String studentId);

    // 🔽 NEW: Find all submissions with a specific assignment scope (INDIVIDUAL, GROUP, CLASS)
    List<Submission> findByAssignment_Scope(AssignmentScope scope);

    // 🔽 NEW: Find submissions for a specific task and group (used in group submissions)
    List<Submission> findByTaskIdAndAssignment_Group_Id(Long taskId, Long groupId);

    // 🔽 NEW: Find submissions by assignment group
    List<Submission> findByAssignment_Group_Id(Long groupId);

    // 🔽 Optional: For checking if a specific student submitted under a group assignment
    List<Submission> findByAssignment_Group_IdAndSubmittedBy(Long groupId, String studentId);
}
