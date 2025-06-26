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
    private Float grade; // Make sure this is Float, not String
    private String feedback;
    private SubmissionStatus status;
    private String submittedBy;

    // Add this field for task reference
    private Long taskId;

    private String gradedBy;
    private LocalDateTime gradedDate;
    private AssignmentScope scope; // INDIVIDUAL / GROUP / CLASS
private Long groupId;   
}
