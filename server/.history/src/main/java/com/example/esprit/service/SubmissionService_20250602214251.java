package com.example.esprit.service;

import com.example.esprit.dto.SubmissionDTO;
import com.example.esprit.model.Submission;
import com.example.esprit.repository.SubmissionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubmissionService {

    private final SubmissionRepository submissionRepository;

    public SubmissionService(SubmissionRepository submissionRepository) {
        this.submissionRepository = submissionRepository;
    }

    public Submission createSubmission(Submission submission) {
        return submissionRepository.save(submission);
    }

    public Optional<SubmissionDTO> getSubmissionById(Long id) {
        return submissionRepository.findById(id)
                .map(submissionMapper::toDTO);
    }

    public List<Submission> getAllSubmissions() {
        return submissionRepository.findAll();
    }

public Submission updateSubmission(Long id, Submission updatedSubmission) {
    return submissionRepository.findById(id)
            .map(existing -> {
                existing.setContent(updatedSubmission.getContent());
                existing.setSubmissionDate(updatedSubmission.getSubmissionDate());
                existing.setGrade(updatedSubmission.getGrade());
                existing.setFeedback(updatedSubmission.getFeedback());
                existing.setStatus(updatedSubmission.getStatus());
                existing.setSubmittedBy(updatedSubmission.getSubmittedBy());
                // Update task only if needed (usually tasks don't change for submissions)
                if(updatedSubmission.getTask() != null) {
                    existing.setTask(updatedSubmission.getTask());
                }
                existing.setGradedBy(updatedSubmission.getGradedBy());
                existing.setGradedDate(updatedSubmission.getGradedDate());
                return submissionRepository.save(existing);
            }).orElseThrow(() -> new RuntimeException("Submission not found with id " + id));
}

    public void deleteSubmission(Long id) {
        submissionRepository.deleteById(id);
    }
}
