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
public ResponseEntity<String> deleteTask(@PathVariable Long taskId) {
    taskService.deleteTask(taskId);
    return ResponseEntity.ok("Task deleted successfully.");
}
@PutMapping("/{taskId}")
public ResponseEntity<TaskDTO> updateTask(
        @PathVariable Long taskId,
        @RequestBody TaskRequestDTO dto
) {

    Task updatedTask = taskService.updateTask(taskId, dto);


    return ResponseEntity.ok(responseDto);
}


}
