package com.example.esprit.controller;

import com.example.esprit.model.StudentClassEnrollment;
import com.example.esprit.service.StudentClassEnrollmentService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class StudentClassEnrollmentController {

    private final StudentClassEnrollmentService enrollmentService;

    @PostMapping("/enroll")
    public ResponseEntity<StudentClassEnrollment> enrollStudent(
            @RequestParam String studentId,
            @RequestParam Long classId) {
        try {
            StudentClassEnrollment enrollment = enrollmentService.enrollStudentToClass(studentId, classId);
            return ResponseEntity.ok(enrollment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/by-class/{classId}")
    public ResponseEntity<List<StudentClassEnrollment>> getStudentsByClass(@PathVariable Long classId) {
        List<StudentClassEnrollment> students = enrollmentService.getStudentsByClassId(classId);
        return ResponseEntity.ok(students);
    }

    @DeleteMapping("/remove/{studentId}")
    public ResponseEntity<Void> removeEnrollment(@PathVariable String studentId) {
        enrollmentService.removeStudentEnrollment(studentId);
        return ResponseEntity.noContent().build();
    }
}
