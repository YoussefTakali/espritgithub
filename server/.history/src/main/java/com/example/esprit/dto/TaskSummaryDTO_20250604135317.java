package com.example.esprit.dto;

import java.time.LocalDateTime;
import com.example.esprit.model.TaskStatus;

import lombok.Data;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskSummaryDto {
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
    
    public static TaskSummaryDto fromTask(Task task) {
        return TaskSummaryDto.builder()
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