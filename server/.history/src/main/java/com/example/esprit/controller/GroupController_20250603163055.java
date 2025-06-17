
package com.example.esprit.controller;

import com.example.esprit.dto.GroupRequestDTO;
import com.example.esprit.model.Group;
import com.example.esprit.service.GroupService;
import lombok.RequiredArgsConstructor;
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
@PutMapping("/{groupId}")
public ResponseEntity<Group> updateGroup(@PathVariable Long groupId, @RequestBody GroupRequestDTO dto) {
    Group updatedGroup = groupService.updateGroup(groupId, dto);
    return ResponseEntity.ok(updatedGroup);
}

}
