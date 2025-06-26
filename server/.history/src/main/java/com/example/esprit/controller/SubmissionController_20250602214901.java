import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.esprit.dto.SubmissionDTO;
import com.example.esprit.service.SubmissionService;
import com.example.esprit.util.SubmissionMapper;

@RestController
public class SubmissionController {

    private final SubmissionService submissionService;
    private final SubmissionMapper submissionMapper;

    public SubmissionController(SubmissionService submissionService, SubmissionMapper submissionMapper) {
        this.submissionService = submissionService;
        this.submissionMapper = submissionMapper;
    }

    @PostMapping
    public ResponseEntity<SubmissionDTO> createSubmission(@RequestBody SubmissionDTO submissionDTO) {
        Submission submission = submissionMapper.toEntity(submissionDTO);
        Submission created = submissionService.createSubmission(submission);
        return new ResponseEntity<>(submissionMapper.toDTO(created), HttpStatus.CREATED);
    }

    // other methods...
}
