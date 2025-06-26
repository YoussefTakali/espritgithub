package com.example.esprit.controller;

import com.example.esprit.dto.SubmissionDTO;
import com.example.esprit.util.SubmissionMapper;
import com.example.esprit.model.Submission;
import com.example.esprit.service.SubmissionService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {

    private final SubmissionService submissionService;

    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    // Create - accept DTO and convert to entity
    @PostMapping
    public ResponseEntity<SubmissionDTO> createSubmission(@RequestBody SubmissionDTO submissionDTO) {
        Submission submission = SubmissionMapper.toEntity(submissionDTO);
        Submission created = submissionService.createSubmission(submission);
        return new ResponseEntity<>(SubmissionMapper.toDTO(created), HttpStatus.CREATED);
    }

    // Read all - return list of DTOs
    @GetMapping
    public ResponseEntity<List<SubmissionDTO>> getAllSubmissions() {
        List<SubmissionDTO> dtos = submissionService.getAllSubmissions()
                .stream()
                .map(SubmissionMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // Read one by id - return DTO
    @GetMapping("/{id}")
    public ResponseEntity<SubmissionDTO> getSubmissionById(@PathVariable Long id) {
        return submissionService.getSubmissionById(id)
                .map(SubmissionMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update - accept DTO, convert, update, return DTO
    @PutMapping("/{id}")
    public ResponseEntity<SubmissionDTO> updateSubmission(@PathVariable Long id, @RequestBody SubmissionDTO submissionDTO) {
        try {
            Submission updatedEntity = SubmissionMapper.toEntity(submissionDTO);
            Submission updated = submissionService.updateSubmission(id, updatedEntity);
            return ResponseEntity.ok(SubmissionMapper.toDTO(updated));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubmission(@PathVariable Long id) {
        submissionService.deleteSubmission(id);
        return ResponseEntity.noContent().build();
    }
}
