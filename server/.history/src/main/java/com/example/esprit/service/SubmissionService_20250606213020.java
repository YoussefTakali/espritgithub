package com.example.esprit.service;

import com.example.esprit.dto.SubmissionDTO;
import com.example.esprit.model.*;
import com.example.esprit.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final TaskRepository taskRepository;
    private final GroupRepository groupRepository;

    public SubmissionDTO createSubmission(SubmissionDTO dto) {
        System.out.println(dto.getDescription());
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
                    .description(dto.getDescription())
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
                    .description(dto.getDescription())
                    .content(dto.getContent())
                    .submittedAt(LocalDateTime.now())
                    .build();

            submission = submissionRepository.save(submission);
            return toDto(submission);
        }

        throw new IllegalArgumentException("Unsupported assignment scope");
    }

    public SubmissionDTO getSubmission(Long id) {
        Submission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Submission not found"));
        return toDto(submission);
    }

    public List<SubmissionDTO> getAllSubmissions() {
        return submissionRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

public SubmissionDTO updateSubmission(Long id, SubmissionDTO dto) {
    System.out.println(dto.getGrade());

    Submission submission = submissionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Submission not found"));

    if (dto.getContent() != null) {
        submission.setContent(dto.getContent());
    }

    if (dto.getGrade() != null) {
        Task task = submission.getTask();
        if (task.getDueDate() != null && LocalDateTime.now().isAfter(task.getDueDate())) {
            // Grade is given after due date => divide by 2
            submission.setGrade(dto.getGrade() / 2);
        } else {
            submission.setGrade(dto.getGrade());
        }
    }

    // update other fields as needed
    submission = submissionRepository.save(submission);
    System.out.println(submission.getGrade());

    return toDto(submission);
}

    public void deleteSubmission(Long id) {
        if (!submissionRepository.existsById(id))
            throw new IllegalArgumentException("Submission not found");
        submissionRepository.deleteById(id);
    }

    private SubmissionDTO toDto(Submission submission) {
        System.out.println(submission.getGrade());
        return SubmissionDTO.builder()
                .grade(submission.getGrade())
                .id(submission.getId())
                .taskId(submission.getTask().getId())
                .studentId(submission.getStudentId())
                .groupId(submission.getGroup() != null ? submission.getGroup().getId() : null)
                .content(submission.getContent())
                .submittedAt(submission.getSubmittedAt())
                .description(submission.getDescription())
                .status(submission.getStatus() != null ? submission.getStatus().name() : null)
                .build();
    }
    public List<SubmissionDTO> getSubmissionsByTaskId(Long taskId) {
        return submissionRepository.findByTaskId(taskId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    public List<SubmissionDTO> getSubmissionsByTeacherId(String teacherId) {
        return submissionRepository.findByTask_CreatedBy(teacherId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    public Map<Long, Double> getGroupGradeCoverage() {
    // 1. Get all group tasks that are graded
    List<TaskAssignment> gradedGroupAssignments = taskRepository.findAll().stream()
            .filter(Task::isGraded)
            .flatMap(task -> task.getAssignments().stream())
            .filter(assignment -> assignment.getScope() == AssignmentScope.GROUP && assignment.getGroup() != null)
            .collect(Collectors.toList());

    // 2. Group tasks by groupId
    Map<Long, List<Task>> gradedTasksPerGroup = gradedGroupAssignments.stream()
            .collect(Collectors.groupingBy(
                    assignment -> assignment.getGroup().getId(),
                    Collectors.mapping(TaskAssignment::getTask, Collectors.toList())
            ));

    // 3. Get all submissions with non-null grade
    List<Submission> allGradedSubmissions = submissionRepository.findAll().stream()
            .filter(sub -> sub.getGroup() != null && sub.getGrade() != null)
            .collect(Collectors.toList());

    // 4. Group graded submissions by groupId
    Map<Long, Long> gradedSubmissionsCountPerGroup = allGradedSubmissions.stream()
            .collect(Collectors.groupingBy(
                    sub -> sub.getGroup().getId(),
                    Collectors.counting()
            ));

    // 5. Compute ratio for each group
    Map<Long, Double> coveragePerGroup = new HashMap<>();

    for (Map.Entry<Long, List<Task>> entry : gradedTasksPerGroup.entrySet()) {
        Long groupId = entry.getKey();
        int gradedTaskCount = entry.getValue().size();
        long gradedSubmissionCount = gradedSubmissionsCountPerGroup.getOrDefault(groupId, 0L);

        double ratio = gradedTaskCount > 0 ? (double) gradedSubmissionCount / gradedTaskCount : 0.0;
        coveragePerGroup.put(groupId, ratio);
    }

    return coveragePerGroup;
}
}
