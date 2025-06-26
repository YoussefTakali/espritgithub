package com.example.esprit.service;

import com.example.esprit.dto.SubmissionInfo;
import com.example.esprit.dto.SubmissionRequest;
import com.example.esprit.model.*;
import com.example.esprit.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.Set;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubmissionAnalysisService {
    
    private final TaskRepository taskRepository;
    private final SubmissionRepository submissionRepository;
    private final JdbcTemplate jdbcTemplate;
    
    /**
     * Analyzes a submission to determine who submitted it and the submission scope
     */
    public SubmissionInfo analyzeSubmission(Long submissionId) {
        Submission submission = submissionRepository.findById(submissionId)
            .orElseThrow(() -> new RuntimeException("Submission not found with id: " + submissionId));
        
        return analyzeSubmission(submission);
    }
    
    /**
     * Analyzes a submission to determine who submitted it and the submission scope
     */
    public SubmissionInfo analyzeSubmission(Submission submission) {
        Task task = submission.getTask();
        String submittedBy = submission.getSubmittedBy();
        
        SubmissionInfo.SubmissionInfoBuilder infoBuilder = SubmissionInfo.builder()
            .submittedBy(submittedBy);
        
        // Check task assignments to determine scope
        for (TaskAssignment assignment : task.getAssignments()) {
            switch (assignment.getScope()) {
                case INDIVIDUAL:
                    return infoBuilder
                        .submissionType("Individual")
                        .studentId(submittedBy)
                        .build();
                    
                case GROUP:
                    Set<String> groupMembers = getGroupMembers(assignment.getGroup().getId());
                    return infoBuilder
                        .submissionType("Group")
                        .groupId(assignment.getGroup().getId())
                        .groupName(assignment.getGroup().getName())
                        .submittedOnBehalfOfGroup(true)
                        .groupMembers(groupMembers)
                        .build();
                    
                case CLASS:
                    Set<String> classMembers = getClassMembers(assignment.getClasse().getId());
                    return infoBuilder
                        .submissionType("Class")
                        .classId(assignment.getClasse().getId())
                        .className(assignment.getClasse().getName())
                        .submittedOnBehalfOfClass(true)
                        .classMembers(classMembers)
                        .build();
            }
        }
        
        // Default case if no assignments found
        return infoBuilder
            .submissionType("Unknown")
            .build();
    }
    
    /**
     * Checks if a submission is for a group
     */
    public boolean isGroupSubmission(Submission submission) {
        return submission.getTask().getAssignments().stream()
            .anyMatch(assignment -> assignment.getScope() == AssignmentScope.GROUP);
    }
    
    /**
     * Checks if a submission is individual
     */
    public boolean isIndividualSubmission(Submission submission) {
        return submission.getTask().getAssignments().stream()
            .anyMatch(assignment -> assignment.getScope() == AssignmentScope.INDIVIDUAL);
    }
    
    /**
     * Checks if a submission is for an entire class
     */
    public boolean isClassSubmission(Submission submission) {
        return submission.getTask().getAssignments().stream()
            .anyMatch(assignment -> assignment.getScope() == AssignmentScope.CLASS);
    }
    
    /**
     * Gets all members of a group using direct SQL query
     */
    private Set<String> getGroupMembers(Long groupId) {
        String sql = "SELECT student_id FROM group_members WHERE group_id = ?";
        List<String> members = jdbcTemplate.queryForList(sql, String.class, groupId);
        return new HashSet<>(members);
    }
    
    /**
     * Gets all students enrolled in a class using direct SQL query
     */
    private Set<String> getClassMembers(Long classId) {
        String sql = "SELECT student_id FROM student_class_enrollments WHERE class_id = ?";
        List<String> members = jdbcTemplate.queryForList(sql, String.class, classId);
        return new HashSet<>(members);
    }
    
    /**
     * Gets submission info for all submissions of a task
     */
    public Set<SubmissionInfo> analyzeTaskSubmissions(Long taskId) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));
        
        return task.getSubmissions().stream()
            .map(this::analyzeSubmission)
            .collect(Collectors.toSet());
    }
    
    /**
     * Checks if a specific student is represented by a submission
     * (useful for group/class submissions to see if a student is covered)
     */
    public Submission createSubmission(SubmissionRequest request) {
    Task task = taskRepository.findById(request.getTaskId())
        .orElseThrow(() -> new RuntimeException("Task not found with id: " + request.getTaskId()));

    Submission submission = Submission.builder()
        .content(request.getContent())
        .submittedBy(request.getSubmittedBy())
        .task(task)
        .submissionDate(LocalDateTime.now())
        .status(SubmissionStatus.SUBMITTED)
        .build();

    return submissionRepository.save(submission);
}
    public boolean isStudentRepresentedBySubmission(String studentId, Submission submission) {
        SubmissionInfo info = analyzeSubmission(submission);
        
        switch (info.getSubmissionType()) {
            case "Individual":
                return studentId.equals(info.getStudentId());
            case "Group":
                return info.getGroupMembers().contains(studentId);
            case "Class":
                return info.getClassMembers().contains(studentId);
            default:
                return false;
        }
    }
}