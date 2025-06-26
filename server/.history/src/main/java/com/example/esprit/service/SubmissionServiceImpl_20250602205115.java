package com.example.esprit.service;

import com.example.esprit.model.Submission;
import com.example.esprit.repository.SubmissionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubmissionServiceImpl implements SubmissionService {

    private final SubmissionRepository submissionRepository;

    public SubmissionServiceImpl(SubmissionRepository submissionRepository) {
        this.submissionRepository = submissionRepository;
    }

    @Override
    public Submission createSubmission(Submission submission) {
        return submissionRepository.save(submission);
    }

    @Override
    public Optional<Submission> getSubmissionById(Long id) {
        return submissionRepository.findById(id);
    }

    @Override
    public List<Submission> getAllSubmissions() {
        return submissionRepository.findAll();
    }

    @Override
    public Submission updateSubmission(Long id, Submission updatedSubmission) {
        return submissionRepository.findById(id)
                .map(existingSubmission -> {
                    existingSubmission.setTitle(updatedSubmission.getTitle());
                    existingSubmission.setContent(updatedSubmission.getContent());
                    existingSubmission.setDate(updatedSubmission.getDate());
                    // add other fields to update here
                    return submissionRepository.save(existingSubmission);
                }).orElseThrow(() -> new RuntimeException("Submission not found with id " + id));
    }

    @Override
    public void deleteSubmission(Long id) {
        submissionRepository.deleteById(id);
    }
}
