package com.example.esprit.dto;

import java.util.List;

public class TaskAssignmentRequest {
    private Long taskId;
    private List<String> studentIds;

    public TaskAssignmentRequest() {
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public List<String> getStudentIds() {
        return studentIds;
    }

    public void setStudentIds(List<String> studentIds) {
        this.studentIds = studentIds;
    }
}
