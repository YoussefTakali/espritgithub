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
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final ProjectRepository projectRepository;
    private final ClassRepository classRepository;

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

 

    existingGroup.setName(dto.getName());
    existingGroup.setMemberIds(dto.getMemberIds());

    return groupRepository.save(existingGroup);
}



}
