package com.example.esprit.dto;

import lombok.Data;

@Data
public class SubmissionRequest {
    private String content;
    private String submittedBy; // student's ID
    private Long taskId;        // ID of the task the submission is for
}
