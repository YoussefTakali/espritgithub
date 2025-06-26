package com.example.esprit.controller;

import com.example.esprit.dto.ClassDTOResponse;
import com.example.esprit.service.ClassService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classes")
public class ClassController {

    private final ClassService classService;

    public ClassController(ClassService classService) {
        this.classService = classService;
    }

   @GetMapping("/by-teacher/{teacherId}")
    public ResponseEntity<List<ClassDTOResponse>> getClassesWithProjectsByTeacher(@PathVariable String teacherId) {
        List<ClassDTOResponse> response = classService.getClassesWithProjectsByTeacherId(teacherId);
        return ResponseEntity.ok(response);
    }
}


public class ClassController {

    private final ClassService classService;

    public ClassController(ClassService classService) {
        this.classService = classService;
    }

   @GetMapping("/by-teacher/{teacherId}")
    public ResponseEntity<List<ClassDTOResponse>> getClassesWithPr