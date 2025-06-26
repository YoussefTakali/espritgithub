package com.example.esprit.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

import java.util.List;

public class GroupUpdateDTO {

    private String name;
    private List<Long> memberIds;

    // Getters and setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Long> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(List<Long> memberIds) {
        this.memberIds = memberIds;
    }
}
