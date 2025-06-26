package com.example.esprit.dto;

import java.time.LocalDateTime;

public class TaskSummaryDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private String status;
    private Long projectId;

    // Constructor
    public TaskSummaryDTO(Long id, String title, String description, LocalDateTime dueDate, String status, Long projectId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.status = status;
        this.projectId = projectId;
    }

    // Getters and setters
    // ...
}
