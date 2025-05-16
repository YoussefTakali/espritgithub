package com.example.esprit.service;

import com.example.esprit.model.Class;
import com.example.esprit.repository.ClassRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassService {

    private final ClassRepository classRepository;

    public ClassService(ClassRepository classRepository) {
        this.classRepository = classRepository;
    }

    public List<Class> getClassesByTeacherId(String teacherId) {
        return classRepository.findByTeacherAssignments_TeacherId(teacherId);
    }
}