package com.example.esprit.controller;

import com.example.esprit.dto.TaskDTO;
import com.example.esprit.dto.TaskRequestDTO;
import com.example.esprit.dto.TaskSummaryDTO;
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

    // Map the updated Task entity to TaskDTO
    TaskDTO responseDto = new TaskDTO();
    responseDto.setId(updatedTask.getId());
    responseDto.setTitle(updatedTask.getTitle());
    responseDto.setDescription(updatedTask.getDescription());
    responseDto.setDueDate(updatedTask.getDueDate());
    responseDto.setCreatedBy(updatedTask.getCreatedBy());
    responseDto.setCreatedDate(updatedTask.getCreatedDate());
    responseDto.setStatus(updatedTask.getStatus());
    responseDto.setStatus(updatedTask.getStatus());
    responseDto.setProjectId(updatedTask.getProject() != null ? updatedTask.getProject().getId() : null);
    responseDto.setisGraded(updatedTask.isGraded());
    responseDto.setisVisible(updatedTask.isVisible());

    // Optional: You can also fetch updated assignments and map them like in getTasksByTeacher()

    return ResponseEntity.ok(responseDto);
}
    // @GetMapping("/group/{groupId}")
    // public List<TaskSummaryDTO> getTasksByGroupId(@PathVariable Long groupId) {
    //     return taskService.getTasksByGroupId(groupId);
    // }
    @GetMapping("/member/{memberId}")
    public List<Task> getVisibleTasksByMemberId(@PathVariable String memberId) {
        return taskService.getVisibleTasksByMemberId(memberId);
    }
}
