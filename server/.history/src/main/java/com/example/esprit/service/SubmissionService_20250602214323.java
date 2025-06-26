package com.example.esprit.service;

import com.example.esprit.dto.SubmissionDTO;
import com.example.esprit.util.SubmissionMapper;
import com.example.esprit.model.Submission;
import com.example.esprit.repository.SubmissionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final SubmissionMapper submissionMapper;

    public SubmissionService(SubmissionRepository submissionRepository, SubmissionMapper submissionMapper) {
        this.submissionRepository = submissionRepository;
        this.submissionMapper = submissionMapper;
    }

    public SubmissionDTO createSubmission(Submission submission) {
        Submission saved = submissionRepository.save(submission);
        return submissionMapper.toDTO(saved);
    }

    public Optional<SubmissionDTO> getSubmissionById(Long id) {
        return submissionRepository.findById(id)
                .map(submissionMapper::toDTO);
    }

    public List<SubmissionDTO> getAllSubmissions() {
        return submissionRepository.findAll()
                .stream()
                .map(submissionMapper::toDTO)
                .collect(Collectors.toList());
    }

    public SubmissionDTO updateSubmission(Long id, Submission updatedSubmission) {
        return submissionRepository.findById(id)
                .map(existing -> {
                    existing.setContent(updatedSubmission.getContent());
                    existing.setSubmissionDate(updatedSubmission.getSubmissionDate());
                    existing.setGrade(updatedSubmission.getGrade());
                    existing.setFeedback(updatedSubmission.getFeedback());
                    existing.setStatus(updatedSubmission.getStatus());
                    existing.setSubmittedBy(updatedSubmission.getSubmittedBy());
                    if (updatedSubmission.getTask() != null) {
                        existing.setTask(updatedSubmission.getTask());
                    }
                    existing.setGradedBy(updatedSubmission.getGradedBy());
                    existing.setGradedDate(updatedSubmission.getGradedDate());
                    Submission saved = submissionRepository.save(existing);
                    return submissionMapper.toDTO(saved);
                }).orElseThrow(() -> new RuntimeException("Submission not found with id " + id));
    }

    public void deleteSubmission(Long id) {
        submissionRepository.deleteById(id);
    }
}
