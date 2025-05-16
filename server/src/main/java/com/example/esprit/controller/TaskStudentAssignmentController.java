package com.example.esprit.controller;

import com.example.esprit.dto.TaskAssignmentRequest;
import com.example.esprit.model.TaskStudentAssignment;
import com.example.esprit.service.TaskStudentAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/task-assignments")
@RequiredArgsConstructor
public class TaskStudentAssignmentController {
    private final TaskStudentAssignmentService assignmentService;

    @PostMapping
    public ResponseEntity<List<TaskStudentAssignment>> assignTaskToMultipleStudents(
            @RequestBody TaskAssignmentRequest request) {
        List<TaskStudentAssignment> assignments = assignmentService.assignTaskToStudents(request.getTaskId(), request.getStudentIds());
        return ResponseEntity.ok(assignments);
    }
}
