package com.example.esprit.dto;

import com.example.esprit.model.AssignmentScope;

public class TaskAssignmentDTO {
    private Long id;
    private AssignmentScope scope;
    
    private Long groupId;
    private Long classId;
    private String studentId;
    private GroupDTO group;   // Only keep group field

    public GroupDTO getGroup() {
        return group;
    }
    public void setGroup(GroupDTO group) {
        this.group = group;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public AssignmentScope getScope() {
        return scope;
    }
    public void setScope(AssignmentScope scope) {
        this.scope = scope;
    }
    public Long getGroupId() {
        return groupId;
    }
    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }
    public Long getClassId() {
        return classId;
    }
    public void setClassId(Long classId) {
        this.classId = classId;
    }
    public String getStudentId() {
        return studentId;
    }
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    // getters and setters
}
