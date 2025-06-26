package com.example.esprit.util;

import com.example.esprit.dto.ClassDTO;
import com.example.esprit.dto.GroupDTO;
import com.example.esprit.dto.ProjectDTO;
import com.example.esprit.model.Project;
import com.example.esprit.model.Group;
import com.example.esprit.model.Class;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ProjectMapper {

    public static ProjectDTO toDto(Project project) {
        if (project == null) {
            return null;
        }

        ProjectDTO dto = new ProjectDTO();
        dto.setId(project.getId());
        dto.setName(project.getName());
        dto.setDescription(project.getDescription());
        dto.setCreatedDate(project.getCreatedDate());
        dto.setDueDate(project.getDueDate());
        dto.setStatus(project.getStatus());
        dto.setCreatedBy(project.getCreatedBy());
        dto.setCollaborators(project.getCollaborators());
        dto.setAssociatedClasses(mapClassesToDto(project.getAssociatedClasses()));
        dto.setGroups(mapGroupsToDto(project.getGroups()));
        
        return dto;
    }

    public static List<ProjectDTO> toDtoList(List<Project> projects) {
        if (projects == null || projects.isEmpty()) {
            return Collections.emptyList();
        }

        List<ProjectDTO> result = new ArrayList<>();
        for (Project project : projects) {
            result.add(toDto(project));
        }
        return result;
    }

    private static List<ClassDTO> mapClassesToDto(Set<Class> classes) {
        if (classes == null || classes.isEmpty()) {
            return Collections.emptyList();
        }

        List<ClassDTO> result = new ArrayList<>();
        for (Class clazz : classes) {
            result.add(new ClassDTO(clazz.getId(), clazz.getName()));
        }
        return result;
    }
    
    private static List<GroupDTO> mapGroupsToDto(Set<Group> groups) {
        if (groups == null || groups.isEmpty()) {
            return Collections.emptyList();
        }

        return groups.stream()
            .map(group -> GroupDTO.builder()
                .id(group.getId())
                .name(group.getName())
                .projectId(group.getProject() != null ? group.getProject().getId() : null)
                .classId(group.getClasse() != null ? group.getClasse().getId() : null)
                .memberIds(group.getMemberIds())
                .build())
            .toList();
    }
}