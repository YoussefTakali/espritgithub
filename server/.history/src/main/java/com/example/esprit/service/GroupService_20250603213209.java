package com.example.esprit.service;

import com.example.esprit.dto.GroupRequestDTO;
import com.example.esprit.dto.GroupUpdateDTO;
import com.example.esprit.model.Class;
import com.example.esprit.model.Group;
import com.example.esprit.model.Project;
import com.example.esprit.repository.ClassRepository;
import com.example.esprit.repository.GroupRepository;
import com.example.esprit.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final ProjectRepository projectRepository;
    private final ClassRepository classRepository;
    private final Assignm
    public Group createGroup(GroupRequestDTO dto) {
        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        Class classe = classRepository.findById(dto.getClassId())
                .orElseThrow(() -> new RuntimeException("Class not found"));

        Group group = Group.builder()
                .name(dto.getName())
                .project(project)
                .classe(classe)
                .memberIds(dto.getMemberIds())
                .build();

        return groupRepository.save(group);
    }
public Group updateGroup(Long groupId, GroupUpdateDTO dto) {
    Group existingGroup = groupRepository.findById(groupId)
            .orElseThrow(() -> new RuntimeException("Group not found"));

    if (dto.getName() != null) {
        existingGroup.setName(dto.getName());
    }

    if (dto.getMemberIds() != null) {
        Set<String> memberIdsSet = dto.getMemberIds().stream()
                .map(String::valueOf)   // convert Long to String
                .collect(Collectors.toSet());
        existingGroup.setMemberIds(memberIdsSet);
    }

    return groupRepository.save(existingGroup);
}
public void deleteGroup(Long groupId) {
    // Optional: check if the group exists first
    Group group = groupRepository.findById(groupId)
            .orElseThrow(() -> new RuntimeException("Group not found"));

    // Step 1: delete all related task assignments manually
    taskAssignmentRepository.deleteByGroupId(groupId);

    // Step 2: delete the group
    groupRepository.deleteById(groupId);
}


}
