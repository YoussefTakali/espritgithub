package esprit.git_push_test.Controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import esprit.git_push_test.Service.PushedFileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/github")
public class WebhookController {

    private final PushedFileService pushedFileService;
    private final ObjectMapper objectMapper;

    @Value("${github.token}")
    private String githubToken;

    public WebhookController(PushedFileService pushedFileService) {
        this.pushedFileService = pushedFileService;
        this.objectMapper = new ObjectMapper();
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody String payload,
                                              @RequestHeader("X-GitHub-Event") String eventType) {
        try {
            if ("push".equals(eventType)) {
                JsonNode rootNode = objectMapper.readTree(payload);
                
                // Extract repository information
                String repoOwner = rootNode.path("repository").path("owner").path("name").asText();
                String repoName = rootNode.path("repository").path("name").asText();
                
                // Get the latest commit
                JsonNode commits = rootNode.path("commits");
                if (commits.isArray() && commits.size() > 0) {
                    JsonNode latestCommit = commits.get(commits.size() - 1);
                    String commitId = latestCommit.path("id").asText();
                    
                    // Get modified files
                    List<String> modifiedFiles = new ArrayList<>();
                    JsonNode files = latestCommit.path("modified");
                    if (files.isArray()) {
                        files.forEach(file -> modifiedFiles.add(file.asText()));
                    }
                    
                    // Get added files
                    JsonNode addedFiles = latestCommit.path("added");
                    if (addedFiles.isArray()) {
                        addedFiles.forEach(file -> modifiedFiles.add(file.asText()));
                    }
                    
                    // Save the files to database
                    if (!modifiedFiles.isEmpty()) {
                        pushedFileService.saveFilesFromPushEvent(repoOwner, repoName, commitId, modifiedFiles, githubToken);
                    }
                }
                
                return ResponseEntity.ok("Push event processed successfully");
            }
            return ResponseEntity.ok("Event type not handled: " + eventType);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error processing webhook: " + e.getMessage());
        }
    }
}