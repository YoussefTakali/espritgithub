package com.example.esprit.service;

import com.example.esprit.dto.GroupRequestDTO;
import com.example.esprit.dto.GroupResponseDTO;
import com.example.esprit.dto.GroupUpdateDTO;
import com.example.esprit.model.Class;
import com.example.esprit.model.Group;
import com.example.esprit.model.Project;
import com.example.esprit.repository.ClassRepository;
import com.example.esprit.repository.GroupRepository;
import com.example.esprit.repository.ProjectRepository;
import com.example.esprit.repository.TaskAssignmentRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final ProjectRepository projectRepository;
    private final ClassRepository classRepository;
    private final TaskAssignmentRepository taskAssignmentRepository;
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
@Transactional
public void deleteGroup(Long groupId) {

    taskAssignmentRepository.deleteByGroupId(groupId);
    groupRepository.deleteById(groupId);
}
    public List<Group> getGroupsByMemberId(String memberId) {
        return groupRepository.findByMemberId(memberId);
    }
public List<GroupResponseDTO> getGroups() {
    return groupRepository.findAll().stream()
            .map(this::toGroupResponseDTO)
            .collect(Collectors.toList());
}

private GroupResponseDTO toGroupResponseDTO(Group group) {
    Set<Long> memberIdsLong = null;
    if (group.getMemberIds() != null) {
        memberIdsLong = group.getMemberIds().stream()
                .map(Long::valueOf)  // convert String to Long
                .collect(Collectors.toSet());
    }

    return GroupResponseDTO.builder()
            .id(group.getId())
            .name(group.getName())
            .projectId(group.getProject() != null ? group.getProject().getId() : null)
            .projectName(group.getProject() != null ? group.getProject().getName() : null)
            .classId(group.getClasse() != null ? group.getClasse().getId() : null)
            .className(group.getClasse() != null ? group.getClasse().getName() : null)
            .memberIds(memberIdsLong)
            .build();
}



}
