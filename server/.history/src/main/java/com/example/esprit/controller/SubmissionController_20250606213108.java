package com.example.esprit.controller;

import com.example.esprit.dto.SubmissionDTO;
import com.example.esprit.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    @PostMapping
    public ResponseEntity<SubmissionDTO> createSubmission(@RequestBody SubmissionDTO dto) {
        return ResponseEntity.ok(submissionService.createSubmission(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubmissionDTO> getSubmission(@PathVariable Long id) {
        return ResponseEntity.ok(submissionService.getSubmission(id));
    }
    @GetMapping("/task/{taskId}")
    public List<SubmissionDTO> getSubmissionsByTaskId(@PathVariable Long taskId) {
        return submissionService.getSubmissionsByTaskId(taskId);
    }
    @GetMapping("/teacher/{teacherId}")
    public List<SubmissionDTO> getSubmissionsByTeacherId(@PathVariable String teacherId) {
        return submissionService.getSubmissionsByTeacherId(teacherId);
    }

    @GetMapping
    public ResponseEntity<List<SubmissionDTO>> getAllSubmissions() {
        return ResponseEntity.ok(submissionService.getAllSubmissions());
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubmissionDTO> updateSubmission(@PathVariable Long id, @RequestBody SubmissionDTO dto) {
        System.out.println(dto.getGrade());
        return ResponseEntity.ok(submissionService.updateSubmission(id, dto));
    }
  @GetMapping("/group-grade-coverage")
    public ResponseEntity<Map<Long, Double>> getGroupGradeCoverage() {
        Map<Long, Double> result = submissionService.getGroupGradeCoverage();
        return ResponseEntity.ok(result);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubmission(@PathVariable Long id) {
        submissionService.deleteSubmission(id);
        return ResponseEntity.noContent().build();
    }
}
