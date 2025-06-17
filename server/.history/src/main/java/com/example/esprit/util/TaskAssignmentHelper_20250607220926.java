package com.example.esprit.util;

import com.example.esprit.dto.TaskRequestDTO;
import com.example.esprit.model.*;
import com.example.esprit.model.Class;
import com.example.esprit.repository.ClassRepository;
import com.example.esprit.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TaskAssignmentHelper {

    private final GroupRepository groupRepository;
    private final ClassRepository classRepository;

    public List<TaskAssignment> createAssignments(Task task, TaskRequestDTO dto) {
        if (dto.getScope() == null) {
            throw new IllegalArgumentException("Assignment scope must be provided");
        }

        switch (dto.getScope()) {
            case GROUP:
                return createGroupAssignments(task, dto);
            case CLASS:
                return createClassAssignments(task, dto);
            case INDIVIDUAL:
                return createIndividualAssignments(task, dto);
            case PROJECT:
                return createProjectAssignments(task);
            default:
                throw new IllegalArgumentException("Unknown assignment scope");
        }
    }

    private List<TaskAssignment> createGroupAssignments(Task task, TaskRequestDTO dto) {
        if (dto.getGroupIds() == null || dto.getGroupIds().isEmpty()) {
            throw new IllegalArgumentException("Group IDs required for GROUP scope");
        }
        List<Group> groups = groupRepository.findAllById(dto.getGroupIds());
        List<TaskAssignment> assignments = new ArrayList<>();
        for (Group group : groups) {
            assignments.add(TaskAssignment.builder()
                    .task(task)
                    .scope(AssignmentScope.GROUP)
                    .group(group)
                    .build());
        }
        return assignments;
    }

    private List<TaskAssignment> createClassAssignments(Task task, TaskRequestDTO dto) {
        if (dto.getClassIds() == null || dto.getClassIds().isEmpty()) {
            throw new IllegalArgumentException("Class IDs required for CLASS scope");
        }
        List<Class> classes = classRepository.findAllById(dto.getClassIds());
        List<TaskAssignment> assignments = new ArrayList<>();
        for (Class classe : classes) {
            assignments.add(TaskAssignment.builder()
                    .task(task)
                    .scope(AssignmentScope.CLASS)
                    .classe(classe)
                    .build());
        }
        return assignments;
    }

    private List<TaskAssignment> createIndividualAssignments(Task task, TaskRequestDTO dto) {
        if (dto.getStudentIds() == null || dto.getStudentIds().isEmpty()) {
            throw new IllegalArgumentException("Student IDs required for INDIVIDUAL scope");
        }
        List<TaskAssignment> assignments = new ArrayList<>();
        for (String studentId : dto.getStudentIds()) {
            assignments.add(TaskAssignment.builder()
                    .task(task)
                    .scope(AssignmentScope.INDIVIDUAL)
                    .studentId(studentId)
                    .build());
        }
        return assignments;
    }

    private List<TaskAssignment> createProjectAssignments(Task task) {
        List<TaskAssignment> assignments = new ArrayList<>();
        assignments.add(TaskAssignment.builder()
                .task(task)
                .scope(AssignmentScope.PROJECT)
                .build());
        return assignments;
    }
}
