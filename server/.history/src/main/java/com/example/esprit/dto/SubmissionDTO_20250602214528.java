package com.example.esprit.dto;

import java.time.LocalDateTime;

import com.example.esprit.model.SubmissionStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubmissionDTO {
    private Long id;
    private String content;
    private LocalDateTime submissionDate;
    private String submittedBy;
    private SubmissionStatus status;
    private Float grade;
    private String feedback;
    private String gradedBy;
    private LocalDateTime gradedDate;

    private String assignmentScope; // GROUP, CLASS, INDIVIDUAL
    private Long groupId;
    private String groupName;
}
