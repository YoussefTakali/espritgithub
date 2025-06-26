package com.example.esprit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.esprit.model.Submission;
import com.example.esprit.model.SubmissionStatus;

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
}