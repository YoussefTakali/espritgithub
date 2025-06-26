package com.example.esprit.dto;

import java.time.LocalDateTime;
import com.example.esprit.model.TaskStatus;

public class TaskSummaryDTO {
    private Long id;
    private String title;
    private LocalDateTime dueDate;
    private TaskStatus status;
    private Long projectId;  // <-- Add projectId here

    public TaskSummaryDTO(Long id, String title, LocalDateTime dueDate, TaskStatus status, Long projectId) {
        this.id = id;
        this.title = title;
        this.dueDate = dueDate;
        this.status = status;
        this.projectId = projectId;
    }

    // getters and setters (or use lombok if you want)
}
