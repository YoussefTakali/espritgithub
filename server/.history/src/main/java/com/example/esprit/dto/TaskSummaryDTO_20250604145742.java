package com.example.esprit.dto;

import java.time.LocalDateTime;

import com.example.esprit.model.Task;
import com.example.esprit.model.TaskStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskSummaryDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private TaskStatus status;
    private LocalDateTime createdDate;
    private String createdBy;
    private boolean isGraded;
    private boolean isVisible;
    private Long projectId;

    public static TaskSummaryDTO fromTask(Task task) {
        return TaskSummaryDTO.builder()
            .id(task.getId())
            .title(task.getTitle())
            .description(task.getDescription())
            .dueDate(task.getDueDate())
            .status(task.getStatus())
            .createdDate(task.getCreatedDate())
            .createdBy(task.getCreatedBy())
            .isGraded(task.isGraded())
            .isVisible(task.isVisible())
            .projectId(task.getProject().getId())
            .build();
    }
}
