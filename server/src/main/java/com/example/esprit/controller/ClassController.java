package com.example.esprit.controller;

import com.example.esprit.model.Class;
import com.example.esprit.service.ClassService;
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
    public List<Class> getClassesByTeacherId(@PathVariable String teacherId) {
        return classService.getClassesByTeacherId(teacherId);
    }
}

