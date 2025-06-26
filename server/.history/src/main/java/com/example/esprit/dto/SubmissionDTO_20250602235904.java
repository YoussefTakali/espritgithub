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
    private String description; // ID of the student who submitted
    private Long groupId;
    private String content;
    private LocalDateTime submittedAt;
    private String status; // optional
}
