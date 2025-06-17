package com.example.esprit.service;

import com.example.esprit.dto.SubmissionDTO;
import com.example.esprit.dto.SubmissionDto;
import com.example.esprit.model.*;
import com.example.esprit.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final TaskRepository taskRepository;
    private final GroupRepository groupRepository;

    public SubmissionDto createSubmission(SubmissionDto dto) {
        Task task = taskRepository.findById(dto.getTaskId())
            .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        // For simplicity, find the first assignment for this task (improve if needed)
        TaskAssignment assignment = task.getAssignments().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("Task has no assignment"));

        AssignmentScope scope = assignment.getScope();

        if (scope == AssignmentScope.INDIVIDUAL) {
            if (dto.getStudentId() == null)
                throw new IllegalArgumentException("Student ID required for individual submission");
            if (submissionRepository.existsByTaskIdAndStudentId(task.getId(), dto.getStudentId()))
                throw new IllegalStateException("Student already submitted");

            Submission submission = Submission.builder()
                    .task(task)
                    .studentId(dto.getStudentId())
                    .content(dto.getContent())
                    .submittedAt(LocalDateTime.now())
                    .build();

            submission = submissionRepository.save(submission);
            return toDto(submission);

        } else if (scope == AssignmentScope.GROUP) {
            if (dto.getGroupId() == null)
                throw new IllegalArgumentException("Group ID required for group submission");
            if (submissionRepository.existsByTaskIdAndGroupId(task.getId(), dto.getGroupId()))
                throw new IllegalStateException("Group already submitted");

            Group group = groupRepository.findById(dto.getGroupId())
                    .orElseThrow(() -> new IllegalArgumentException("Group not found"));

            Submission submission = Submission.builder()
                    .task(task)
                    .group(group)
                    .content(dto.getContent())
                    .submittedAt(LocalDateTime.now())
                    .build();

            submission = submissionRepository.save(submission);
            return toDto(submission);
        }

        throw new IllegalArgumentException("Unsupported assignment scope");
    }

    public SubmissionDto getSubmission(Long id) {
        Submission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Submission not found"));
        return toDto(submission);
    }

    public List<SubmissionDto> getAllSubmissions() {
        return submissionRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public SubmissionDto updateSubmission(Long id, SubmissionDto dto) {
        Submission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Submission not found"));

        if (dto.getContent() != null) {
            submission.setContent(dto.getContent());
        }
        // update other fields as needed (e.g., status)
        submission = submissionRepository.save(submission);
        return toDto(submission);
    }

    public void deleteSubmission(Long id) {
        if (!submissionRepository.existsById(id))
            throw new IllegalArgumentException("Submission not found");
        submissionRepository.deleteById(id);
    }

    private SubmissionDTO toDto(Submission submission) {
        return SubmissionDto.builder()
                .id(submission.getId())
                .taskId(submission.getTask().getId())
                .studentId(submission.getStudentId())
                .groupId(submission.getGroup() != null ? submission.getGroup().getId() : null)
                .content(submission.getContent())
                .submittedAt(submission.getSubmittedAt())
                .status(submission.getStatus() != null ? submission.getStatus().name() : null)
                .build();
    }
}
