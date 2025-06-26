package com.example.esprit.util.mapper;

import com.example.esprit.dto.SubmissionDTO;
import com.example.esprit.model.Submission;
import org.springframework.stereotype.Component;

@Component
public class SubmissionMapper {

    public SubmissionDTO toDTO(Submission submission) {
        if (submission == null) return null;

        return SubmissionDTO.builder()
            .id(submission.getId())
            .content(submission.getContent())
            .submissionDate(submission.getSubmissionDate())
            .grade(submission.getGrade())
            .feedback(submission.getFeedback())
            .status(submission.getStatus())
            .submittedBy(submission.getSubmittedBy())
            .taskId(submission.getTask() != null ? submission.getTask().getId() : null)
            .gradedBy(submission.getGradedBy())
            .gradedDate(submission.getGradedDate())
            .build();
    }

    public Submission toEntity(SubmissionDTO dto) {
        if (dto == null) return null;

        Submission submission = new Submission();
        submission.setId(dto.getId());
        submission.setContent(dto.getContent());
        submission.setSubmissionDate(dto.getSubmissionDate());
        submission.setGrade(dto.getGrade());
        submission.setFeedback(dto.getFeedback());
        submission.setStatus(dto.getStatus());
        submission.setSubmittedBy(dto.getSubmittedBy());
        // Note: You may want to set Task by fetching from DB if taskId is provided
        // submission.setTask(...);
        submission.setGradedBy(dto.getGradedBy());
        submission.setGradedDate(dto.getGradedDate());

        return submission;
    }
}
