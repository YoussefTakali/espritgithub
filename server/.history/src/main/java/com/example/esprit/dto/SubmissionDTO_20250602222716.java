package com.example.esprit.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionDTO {

    private Long id;
    private Long taskId;
    private String studentId;
    private Long groupId;
    private String content;
    private LocalDateTime submittedAt;
    private String status; // optional
}
