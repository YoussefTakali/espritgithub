package com.example.esprit.dto;

import com.example.esprit.model.AssignmentScope;
import com.example.esprit.model.SubmissionStatus;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SubmissionDTO {
    private Long id;
    private String content;
    private LocalDateTime submissionDate;
    private Float grade;
    private String feedback;
    private SubmissionStatus status;
    private String submittedBy;
    private Long taskId;
    private String gradedBy;
    private LocalDateTime gradedDate;
    private AssignmentScope scope;  // must be here!
    private Long groupId;           // must be here!
}
