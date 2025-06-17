package com.example.esprit.service;

import com.example.esprit.dto.SubmissionDTO;
import com.example.esprit.model.Submission;
import com.example.esprit.model.Task;
import com.example.esprit.model.TaskAssignment;
import com.example.esprit.repository.SubmissionRepository;
import com.example.esprit.repository.TaskAssignmentRepository;
import com.example.esprit.repository.TaskRepository;
import com.example.esprit.util.SubmissionMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final TaskRepository taskRepository;
    private final TaskAssignmentRepository taskAssignmentRepository;

    public SubmissionService(SubmissionRepository submissionRepository,
                              TaskRepository taskRepository,
                              TaskAssignmentRepository taskAssignmentRepository) {
        this.submissionRepository = submissionRepository;
        this.taskRepository = taskRepository;
        this.taskAssignmentRepository = taskAssignmentRepository;
    }

    public SubmissionDTO createSubmission(SubmissionDTO dto) {
        Submission submission = SubmissionMapper.toEntity(dto);

        // ✅ Set Task
        Task task = taskRepository.findById(dto.getTaskId())
                .orElseThrow(() -> new RuntimeException("Task not found with id " + dto.getTaskId()));
        submission.setTask(task);

        // ✅ Set Assignment based on scope
        TaskAssignment assignment = null;
        if (dto.getGroupId() != null) {
            assignment = taskAssignmentRepository
                    .findByTaskIdAndGroupId(dto.getTaskId(), dto.getGroupId())
                    .orElse(null); // Optional: ignore if not found
        } else {
            assignment = taskAssignmentRepository
                    .findByTaskIdAndStudentId(dto.getTaskId(), dto.getSubmittedBy())
                    .orElse(null);
        }

        submission.setAssignment(assignment);

        Submission saved = submissionRepository.save(submission);
        return SubmissionMapper.toDTO(saved);
    }

    public Optional<SubmissionDTO> getSubmissionById(Long id) {
        return submissionRepository.findById(id)
                .map(SubmissionMapper::toDTO);
    }

    public List<SubmissionDTO> getAllSubmissions() {
        return submissionRepository.findAll()
                .stream()
                .map(SubmissionMapper::toDTO)
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
                    return SubmissionMapper.toDTO(saved);
                }).orElseThrow(() -> new RuntimeException("Submission not found with id " + id));
    }

    public void deleteSubmission(Long id) {
        submissionRepository.deleteById(id);
    }
}
