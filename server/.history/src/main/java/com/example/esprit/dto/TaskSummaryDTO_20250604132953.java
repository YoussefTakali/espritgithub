package com.example.esprit.dto;

import java.time.LocalDate;

public class TaskSummaryDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDate dueDate;
    private String status;
    private Long projectId;

    public TaskSummaryDTO(Long id, String title, String description, LocalDate dueDate, String status, Long projectId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.status = status;
        this.projectId = projectId;
    }

    // Getters (or use Lombok if preferred)
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public String getStatus() {
        return status;
    }

    public Long getProjectId() {
        return projectId;
    }
}
