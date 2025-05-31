package com.example.esprit.util;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.esprit.dto.ClassDTOResponse;
import com.example.esprit.dto.GroupDTOResponse;
import com.example.esprit.dto.ProjectDTOResponse;
import com.example.esprit.model.Class;
import com.example.esprit.model.Group;
import com.example.esprit.model.Project;

public class ClassProjectResponseMapper {

    public static List<ClassDTOResponse> mapClassesToResponse(Set<Class> classes) {
        return classes.stream().map(clz -> {
            List<ProjectDTOResponse> projects = clz.getProjects().stream().map(proj -> {
                // Filter groups belonging to this class and project
                List<GroupDTOResponse> groups = proj.getGroups().stream()
                    .filter(g -> g.getClasse().getId().equals(clz.getId()))
                    .map(g -> GroupDTOResponse.builder()
                        .id(g.getId())
                        .name(g.getName())
                        .memberIds(g.getMemberIds())
                        .build())
                    .collect(Collectors.toList());

                return ProjectDTOResponse.builder()
                    .id(proj.getId())
                    .name(proj.getName())
                    .description(proj.getDescription())
                    .createdDate(proj.getCreatedDate())
                    .dueDate(proj.getDueDate())
                    .status(proj.getStatus())
                    .createdBy(proj.getCreatedBy())
                    .collaborators(proj.getCollaborators())
                    .groups(groups)
                    .build();
            }).collect(Collectors.toList());

            return ClassDTOResponse.builder()
                .id(clz.getId())
                .name(clz.getName())
                .description(clz.getDescription())
                .academicYear(clz.getAcademicYear())
                .subject(clz.getSubject())
                .projects(projects)
                .build();
        }).collect(Collectors.toList());
    }
}
