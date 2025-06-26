package com.example.esprit.dto;

import java.time.LocalDateTime;

import com.example.esprit.model.AssignmentScope;
import com.example.esprit.model.Task;
import com.example.esprit.model.TaskAssignment;
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
    private AssignmentScope scope; // ✅ Added field

    /**
     * Converts Task to DTO, using memberId to determine correct scope.
     */
    public static TaskSummaryDTO fromTask(Task task, String memberId) {
        AssignmentScope resolvedScope = null;

        if (task.getAssignments() != null) {
            for (TaskAssignment a : task.getAssignments()) {
                if (a.getScope() == AssignmentScope.INDIVIDUAL && memberId.equals(a.getStudentId())) {
                    resolvedScope = AssignmentScope.INDIVIDUAL;
                    break;
                } else if (a.getScope() == AssignmentScope.GROUP &&
                           a.getGroup() != null &&
                           a.getGroup().getMemberIds().contains(memberId)) {
                    resolvedScope = AssignmentScope.GROUP;
                    break;
                } else if (a.getScope() == AssignmentScope.CLASS) {
                    resolvedScope = AssignmentScope.CLASS;
                    // don't break immediately, individual/group takes precedence
                }
            }
        }

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
            .scope(resolvedScope)
            .build();
    }
}
