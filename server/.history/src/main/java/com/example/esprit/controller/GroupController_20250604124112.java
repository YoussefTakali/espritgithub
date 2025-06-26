
package com.example.esprit.controller;

import com.example.esprit.dto.GroupRequestDTO;
import com.example.esprit.dto.GroupResponseDTO;
import com.example.esprit.dto.GroupUpdateDTO;
import com.example.esprit.model.Group;
import com.example.esprit.model.Project;
import com.example.esprit.service.GroupService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    public ResponseEntity<Group> createGroup(@RequestBody GroupRequestDTO dto) {
        Group created = groupService.createGroup(dto);
        return ResponseEntity.ok(created);
    }
    @PutMapping("/{id}")
    public Group updateGroup(@PathVariable Long id, @Valid @RequestBody GroupUpdateDTO dto) {
        return groupService.updateGroup(id, dto);
    }
@DeleteMapping("/{groupId}")
public ResponseEntity<String> deleteGroup(@PathVariable Long groupId) {
    groupService.deleteGroup(groupId);
    return ResponseEntity.noContent().build();  // HTTP 204
}
    @GetMapping("/by-member/{memberId}")
    public ResponseEntity<List<Project>> getProjectsByMemberId(@PathVariable Long memberId) {
        List<Project> projects = projectService.getProjectsByMemberId(memberId);
        return ResponseEntity.ok(projects);
    }
    @GetMapping
    public ResponseEntity<List<GroupResponseDTO>> getGroups() {
        List<GroupResponseDTO> groups = groupService.getGroups();
        return ResponseEntity.ok(groups);
    }

}
