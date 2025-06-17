package com.example.esprit.service;

import com.example.esprit.dto.ClassDTO;
import com.example.esprit.dto.GroupDTO;
import com.example.esprit.dto.TaskAssignmentDTO;
import com.example.esprit.dto.TaskDTO;
import com.example.esprit.dto.TaskRequestDTO;
import com.example.esprit.model.*;
import com.example.esprit.model.Class;
import com.example.esprit.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskAssignmentRepository assignmentRepository;
    private final ProjectRepository projectRepository;
    private final GroupRepository groupRepository;
    private final ClassRepository classRepository;

    @Transactional
    public Task createTask(TaskRequestDTO dto) {
        log.info("Creating task with DTO: {}", dto);
        
        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));
        log.info("Found project: {}", project.getId());

        Task task = Task.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .dueDate(dto.getDueDate())
                .createdDate(LocalDateTime.now())
                .createdBy(dto.getCreatedBy())
                .status(TaskStatus.PENDING)
                .project(project)
                .build();

        // Save task first to get the ID
        Task savedTask = taskRepository.save(task);
        log.info("Saved task with ID: {}", savedTask.getId());

        // Flush to ensure task is persisted before creating assignments
        taskRepository.flush();

        // Create and save assignments
        List<TaskAssignment> assignments = createAssignments(savedTask, dto);
        log.info("Total assignments created: {}", assignments.size());

        // Verify assignments were actually saved
        long assignmentCount = assignmentRepository.countByTaskId(savedTask.getId());
        log.info("Assignments in database for task {}: {}", savedTask.getId(), assignmentCount);

        return savedTask;
    }

    private List<TaskAssignment> createAssignments(Task task, TaskRequestDTO dto) {
        log.info("Creating assignments for task {} with scope: {}", task.getId(), dto.getScope());
        
        List<TaskAssignment> assignmentsToSave = new ArrayList<>();

        switch (dto.getScope()) {
            case GROUP:
                if (dto.getGroupIds() == null || dto.getGroupIds().isEmpty()) {
                    throw new IllegalArgumentException("Group IDs required for GROUP scope");
                }
                log.info("Processing GROUP scope with IDs: {}", dto.getGroupIds());
                List<Group> groups = groupRepository.findAllById(dto.getGroupIds());
                log.info("Found {} groups", groups.size());
                
                for (Group group : groups) {
                    TaskAssignment assignment = TaskAssignment.builder()
                            .task(task)
                            .scope(AssignmentScope.GROUP)
                            .group(group)
                            .build();
                    assignmentsToSave.add(assignment);
                    log.info("Created assignment for group: {}", group.getId());
                }
                break;

            case CLASS:
                if (dto.getClassIds() == null || dto.getClassIds().isEmpty()) {
                    throw new IllegalArgumentException("Class IDs required for CLASS scope");
                }
                log.info("Processing CLASS scope with IDs: {}", dto.getClassIds());
                List<Class> classes = classRepository.findAllById(dto.getClassIds());
                log.info("Found {} classes", classes.size());
                
                for (Class classe : classes) {
                    TaskAssignment assignment = TaskAssignment.builder()
                            .task(task)
                            .scope(AssignmentScope.CLASS)
                            .classe(classe)
                            .build();
                    assignmentsToSave.add(assignment);
                    log.info("Created assignment for class: {}", classe.getId());
                }
                break;

            case INDIVIDUAL:
                if (dto.getStudentIds() == null || dto.getStudentIds().isEmpty()) {
                    throw new IllegalArgumentException("Student IDs required for INDIVIDUAL scope");
                }
                log.info("Processing INDIVIDUAL scope with student IDs: {}", dto.getStudentIds());
                
                for (String studentId : dto.getStudentIds()) {
                    TaskAssignment assignment = TaskAssignment.builder()
                            .task(task)
                            .scope(AssignmentScope.INDIVIDUAL)
                            .studentId(studentId)
                            .build();
                    assignmentsToSave.add(assignment);
                    log.info("Created assignment for student: {}", studentId);
                }
                break;

            case PROJECT:
                log.info("Processing PROJECT scope");
                TaskAssignment assignment = TaskAssignment.builder()
                        .task(task)
                        .scope(AssignmentScope.PROJECT)
                        .build();
                assignmentsToSave.add(assignment);
                log.info("Created project-wide assignment");
                break;

            default:
                throw new IllegalArgumentException("Unknown assignment scope: " + dto.getScope());
        }

        log.info("About to save {} assignments", assignmentsToSave.size());
        
        // Log each assignment before saving
        for (int i = 0; i < assignmentsToSave.size(); i++) {
            TaskAssignment assignment = assignmentsToSave.get(i);
            log.info("Assignment {}: task_id={}, scope={}, group_id={}, class_id={}, student_id={}", 
                i, 
                assignment.getTask() != null ? assignment.getTask().getId() : "null",
                assignment.getScope(),
                assignment.getGroup() != null ? assignment.getGroup().getId() : "null",
                assignment.getClasse() != null ? assignment.getClasse().getId() : "null",
                assignment.getStudentId()
            );
        }

        // Save all assignments at once
        try {
            List<TaskAssignment> savedAssignments = assignmentRepository.saveAll(assignmentsToSave);
            log.info("Successfully saved {} assignments", savedAssignments.size());
            
            // Force flush to database
            assignmentRepository.flush();
            
            // Log saved assignment IDs
            for (TaskAssignment saved : savedAssignments) {
                log.info("Saved assignment with ID: {}", saved.getId());
            }
            
            return savedAssignments;
        } catch (Exception e) {
            log.error("Error saving assignments: ", e);
            throw e;
        }
    }
@Transactional(readOnly = true)
public List<TaskDTO> getTasksByTeacher(String teacherId) {
    log.info("Fetching tasks created by teacher: {}", teacherId);

    List<Task> tasks = taskRepository.findByCreatedByWithAssignments(teacherId);

    log.info("{} tasks found for teacher: {}", tasks.size(), teacherId);

    return tasks.stream().map(task -> {
        // Create TaskDTO and map basic fields
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setDueDate(task.getDueDate());
        dto.setCreatedBy(task.getCreatedBy());
        dto.setStatus(task.getStatus());
        dto.setCreatedDate(task.getCreatedDate());
        dto.setProjectId(task.getProject() != null ? task.getProject().getId() : null);
        
        // Fetch assignments from repository and map to DTOs
        List<TaskAssignment> assignments = assignmentRepository.findByTaskId(task.getId());
        
        Set<TaskAssignmentDTO> assignmentDTOs = assignments.stream()
            .map(assignment -> {
                TaskAssignmentDTO assignmentDTO = new TaskAssignmentDTO();
                assignmentDTO.setId(assignment.getId());
                assignmentDTO.setScope(assignment.getScope());
                assignmentDTO.setGroupId(assignment.getGroup() != null ? assignment.getGroup().getId() : null);
                assignmentDTO.setClassId(assignment.getClasse() != null ? assignment.getClasse().getId() : null);
                assignmentDTO.setStudentId(assignment.getStudentId());
                
                // Add group information if available
                if (assignment.getGroup() != null) {
                    GroupDTO groupDTO = new GroupDTO();
                    groupDTO.setId(assignment.getGroup().getId());
                    groupDTO.setName(assignment.getGroup().getName());
                    groupDTO.setMemberIds(assignment.getGroup().getMemberIds());
                    
                    // If group has a class, add that class info to the group
                    if (assignment.getGroup().getClasse() != null) {
                        ClassDTO groupClassDTO = new ClassDTO();
                        groupClassDTO.setId(assignment.getGroup().getClasse().getId());
                        groupClassDTO.setName(assignment.getGroup().getClasse().getName());
                        groupDTO.setClasse(groupClassDTO);
                    }
                    
                    assignmentDTO.setGroup(groupDTO);
                }
                
                return assignmentDTO;
            }).collect(Collectors.toSet());

        dto.setAssignments(assignmentDTOs);
        return dto;
    }).collect(Collectors.toList());
}
}