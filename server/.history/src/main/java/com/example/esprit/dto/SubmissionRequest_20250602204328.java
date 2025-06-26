package com.example.esprit.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SubmissionRequest {
    private String content;
    private String submittedBy;
    private Long taskId;
}
