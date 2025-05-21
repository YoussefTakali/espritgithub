package com.example.esprit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupDTO {
    private Long id;
    private String name;
    private Set<String> memberIds; // ✅ Match the entity definition
}
