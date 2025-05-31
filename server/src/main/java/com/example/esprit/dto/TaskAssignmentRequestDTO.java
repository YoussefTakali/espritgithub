// TaskAssignmentRequestDTO.java
package com.example.esprit.dto;

import com.example.esprit.model.AssignmentScope;
import lombok.Data;

import java.util.List;

@Data
public class TaskAssignmentRequestDTO {
    private Long taskId;
    private AssignmentScope scope;
    private List<Long> groupIds;     // if scope == GROUP
    private List<Long> classIds;     // if scope == CLASS
    private List<String> studentIds; // if scope == INDIVIDUAL
    // For PROJECT scope, no targets needed, it's implicit for all groups/classes
}
