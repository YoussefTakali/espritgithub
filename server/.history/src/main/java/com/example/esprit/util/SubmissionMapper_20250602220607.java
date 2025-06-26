package com.example.esprit.util;

import com.example.esprit.dto.SubmissionDTO;
import com.example.esprit.model.AssignmentScope;
import com.example.esprit.model.Submission;
import org.springframework.stereotype.Component;

@Component
public class SubmissionMapper {

 SubmissionDTO.SubmissionDTOBuilder builder = SubmissionDTO.builder()
        .id(submission.getId())
        .content(submission.getContent())
        .submissionDate(submission.getSubmissionDate())
        .grade(submission.getGrade())
        .feedback(submission.getFeedback())
        .status(submission.getStatus())
        .submittedBy(submission.getSubmittedBy())
        .taskId(submission.getTask() != null ? submission.getTask().getId() : null)
        .gradedBy(submission.getGradedBy())
        .gradedDate(submission.getGradedDate());

    // Handle assignment scope and groupId
    if (submission.getAssignment() != null) {
        builder.scope(submission.getAssignment().getScope());

        if (submission.getAssignment().getScope() == AssignmentScope.GROUP &&
            submission.getAssignment().getGroup() != null) {
            builder.groupId(submission.getAssignment().getGroup().getId());
        }
    }

    return builder.build();
}

public static Submission toEntity(SubmissionDTO dto) {
    if (dto == null) return null;

    Submission submission = new Submission();
    submission.setId(dto.getId());
    submission.setContent(dto.getContent());
    submission.setSubmissionDate(dto.getSubmissionDate());
    submission.setGrade(dto.getGrade());
    submission.setFeedback(dto.getFeedback());
    submission.setStatus(dto.getStatus());
    submission.setSubmittedBy(dto.getSubmittedBy());
    submission.setGradedBy(dto.getGradedBy());
    submission.setGradedDate(dto.getGradedDate());

    // ⚠️ Do not attempt to build `Task` or `Assignment` manually here
    // Leave `task` and `assignment` to be set by the service (after fetching from DB)
    
    return submission;
}

