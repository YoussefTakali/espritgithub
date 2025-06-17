package com.example.esprit.service;

import com.example.esprit.dto.ClassDTOResponse;
import com.example.esprit.model.Class;
import com.example.esprit.repository.ClassRepository;
import com.example.esprit.util.ClassProjectResponseMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ClassService {
    @Autowired
    private  ClassRepository classRepository;

    public List<ClassDTOResponse> getClassesWithProjectsByTeacherId(String teacherId) {
        // Fetch classes assigned to the teacher
        Set<Class> classes = new HashSet<>(classRepository.findByTeacherAssignments_TeacherId(teacherId));

        // Map entities to DTO response structure
        return ClassProjectResponseMapper.mapClassesToResponse(classes);
    }

}