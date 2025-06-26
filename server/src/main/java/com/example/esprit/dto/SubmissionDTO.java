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
    private Double grade; // optional, can be null if not graded yet
    private String description; // ID of the student who submitted
    private Long groupId;
    private String content;
    private LocalDateTime submittedAt;
    private String status; // optional
}
