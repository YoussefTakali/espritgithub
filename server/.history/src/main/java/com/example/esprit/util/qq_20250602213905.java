package com.example.esprit.util.mapper;

import com.example.esprit.dto.SubmissionDTO;
import com.example.esprit.model.AssignmentScope;
import com.example.esprit.model.Submission;
import com.example.esprit.model.TaskAssignment;

public class SubmissionMapper {

    public static SubmissionDTO toDto(Submission submission) {
        if (submission == null) {
            return null;
        }

        TaskAssignment assignment = submission.getAssignment();

        return SubmissionDTO.builder()
            .id(submission.getId())
            .content(submission.getContent())
            .submissionDate(submission.getSubmissionDate())
            .submittedBy(submission.getSubmittedBy())
            .status(submission.getStatus() != null ? submission.getStatus().name() : null)
            .grade(submission.getGrade())
            .feedback(submission.getFeedback())
            .gradedBy(submission.getGradedBy())
            .gradedDate(submission.getGradedDate())
            .assignmentScope(
                assignment != null && assignment.getScope() != null 
                ? assignment.getScope().name() 
                : null
            )
            .groupId(
                assignment != null 
                && assignment.getScope() == AssignmentScope.GROUP 
                && assignment.getGroup() != null 
                ? assignment.getGroup().getId() 
                : null
            )
            .groupName(
                assignment != null 
                && assignment.getScope() == AssignmentScope.GROUP 
                && assignment.getGroup() != null 
                ? assignment.getGroup().getName() 
                : null
            )
            .build();
    }
}
