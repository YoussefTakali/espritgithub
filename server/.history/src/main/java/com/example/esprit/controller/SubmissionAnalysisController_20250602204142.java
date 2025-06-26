package com.example.esprit.controller;

package com.example.esprit.controller;

import com.example.esprit.dto.SubmissionInfo;
import com.example.esprit.service.SubmissionAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
public class SubmissionAnalysisController {

    private final SubmissionAnalysisService analysisService;

    // Analyze a single submission by its ID
    @GetMapping("/{id}/analyze")
    public SubmissionInfo analyzeSubmission(@PathVariable Long id) {
        return analysisService.analyzeSubmission(id);
    }

    // Analyze all submissions of a task by task ID
    @GetMapping("/task/{taskId}/analyze")
    public Set<SubmissionInfo> analyzeTaskSubmissions(@PathVariable Long taskId) {
        return analysisService.analyzeTaskSubmissions(taskId);
    }

    // Check if a student is represented by a submission
    @GetMapping("/{submissionId}/represents/{studentId}")
    public boolean isStudentRepresented(
        @PathVariable Long submissionId,
        @PathVariable String studentId
    ) {
        return analysisService.isStudentRepresentedBySubmission(studentId, 
                analysisService.analyzeSubmission(submissionId).getSubmission());
    }
}
