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
        List<TaskAssignment> assignmentsToSave = new ArrayList<>();

        if (dto.getScope() == null) {
            throw new IllegalArgumentException("Assignment scope must be provided");
        }

        switch (dto.getScope()) {
            case GROUP:
                if (dto.getGroupIds() == null || dto.getGroupIds().isEmpty()) {
                    throw new IllegalArgumentException("Group IDs required for GROUP scope");
                }
                List<Group> groups = groupRepository.findAllById(dto.getGroupIds());
                for (Group group : groups) {
                    assignmentsToSave.add(TaskAssignment.builder()
                            .task(task)
                            .scope(AssignmentScope.GROUP)
                            .group(group)
                            .build());
                }
                break;

            case CLASS:
                if (dto.getClassIds() == null || dto.getClassIds().isEmpty()) {
                    throw new IllegalArgumentException("Class IDs required for CLASS scope");
                }
                List<Class> classes = classRepository.findAllById(dto.getClassIds());
                for (Class classe : classes) {
                    assignmentsToSave.add(TaskAssignment.builder()
                            .task(task)
                            .scope(AssignmentScope.CLASS)
                            .classe(classe)
                            .build());
                }
                break;

            case INDIVIDUAL:
                if (dto.getStudentIds() == null || dto.getStudentIds().isEmpty()) {
                    throw new IllegalArgumentException("Student IDs required for INDIVIDUAL scope");
                }
                for (String studentId : dto.getStudentIds()) {
                    assignmentsToSave.add(TaskAssignment.builder()
                            .task(task)
                            .scope(AssignmentScope.INDIVIDUAL)
                            .studentId(studentId)
                            .build());
                }
                break;

            case PROJECT:
                assignmentsToSave.add(TaskAssignment.builder()
                        .task(task)
                        .scope(AssignmentScope.PROJECT)
                        .build());
                break;

            default:
                throw new IllegalArgumentException("Unknown assignment scope");
        }

        return assignmentsToSave;
    }
}
