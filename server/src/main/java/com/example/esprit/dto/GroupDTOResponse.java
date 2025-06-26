package com.example.esprit.dto;

import java.util.Set;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class GroupDTOResponse {
    private Long id;
    private String name;
    private Set<String> memberIds;
}