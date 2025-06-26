package com.example.esprit.controller;

import com.example.esprit.dto.TaskDTO;
import com.example.esprit.dto.TaskRequestDTO;
import com.example.esprit.model.Task;
import com.example.esprit.service.TaskService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody TaskRequestDTO dto) {
        Task created = taskService.createTask(dto);
        return ResponseEntity.ok(created);
    }
@GetMapping("/by-teacher/{teacherId}")
public List<TaskDTO> getAllTasksByTeacher(@PathVariable String teacherId) {
    return taskService.getTasksByTeacher(teacherId);
}
@DeleteMapping("/{taskId}")
public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
    log.info("Received request to delete task with ID: {}", taskId);
    taskService.deleteTask(taskId);
    return ResponseEntity.noContent().build(); // 204 No Content
}

}
