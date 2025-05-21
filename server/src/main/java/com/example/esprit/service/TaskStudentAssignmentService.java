// package com.example.esprit.service;

// import com.example.esprit.model.AssignmentStatus;
// import com.example.esprit.model.Task;
// import com.example.esprit.model.TaskStudentAssignment;
// import com.example.esprit.repository.TaskRepository;
// import com.example.esprit.repository.TaskStudentAssignmentRepository;
// import lombok.RequiredArgsConstructor;
// import org.springframework.stereotype.Service;

// import java.time.LocalDateTime;
// import java.util.ArrayList;
// import java.util.List;

// @Service
// @RequiredArgsConstructor
// public class TaskStudentAssignmentService {
//     private final TaskRepository taskRepository;
//     private final TaskStudentAssignmentRepository assignmentRepository;

//     public TaskStudentAssignment assignTaskToStudent(Long taskId, String studentId) {
//         Task task = taskRepository.findById(taskId)
//             .orElseThrow(() -> new RuntimeException("Task not found"));

//         TaskStudentAssignment assignment = TaskStudentAssignment.builder()
//                 .task(task)
//                 .studentId(studentId)
//                 .assignedDate(LocalDateTime.now())
//                 .status(AssignmentStatus.ASSIGNED)
//                 .build();

//         return assignmentRepository.save(assignment);
//     }

//     // New method to assign task to multiple students
//     public List<TaskStudentAssignment> assignTaskToStudents(Long taskId, List<String> studentIds) {
//         List<TaskStudentAssignment> assignments = new ArrayList<>();
//         for (String studentId : studentIds) {
//             TaskStudentAssignment assignment = assignTaskToStudent(taskId, studentId);
//             assignments.add(assignment);
//         }
//         return assignments;
//     }
// }
