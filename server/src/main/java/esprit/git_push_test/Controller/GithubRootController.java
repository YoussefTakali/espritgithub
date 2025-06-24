package esprit.git_push_test.Controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import esprit.git_push_test.Service.GitHubEventListener;
import esprit.git_push_test.Service.PushedFileService;
import esprit.git_push_test.Service.RepositoryInfoService;
import esprit.git_push_test.Service.WebhookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * This controller handles GitHub webhook requests at the root level.
 */
@RestController
public class GithubRootController {

    @Autowired
    private WebhookService webhookService;

    @Autowired
    private GitHubEventListener gitHubEventListener;

    @Autowired
    private PushedFileService pushedFileService;

    @Autowired
    private RepositoryInfoService repositoryInfoService;

    @Value("${github.token}")
    private String githubToken;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Handle webhooks sent directly to /github
     */
    @PostMapping("/github")
    public ResponseEntity<String> handleGithubWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "X-GitHub-Event", required = false) String event,
            @RequestHeader(value = "X-Hub-Signature-256", required = false) String signature) {

        System.out.println("==== WEBHOOK RECEIVED AT ROOT PATH (/github) ====");
        System.out.println("Event: " + event);
        System.out.println("Signature: " + (signature != null ? "Present" : "Not present"));
        System.out.println("Payload length: " + (payload != null ? payload.length() : 0));
        System.out.println("Payload preview: " + (payload != null && payload.length() > 0 ?
                payload.substring(0, Math.min(payload.length(), 200)) + "..." : "Empty"));
        System.out.println("========================");

        // Process the event using the GitHubEventListener
        if (event != null) {
            gitHubEventListener.processEvent(payload, event);

            // Process push events and save to database
            if ("push".equals(event)) {
                try {
                    processPushEvent(payload);
                } catch (Exception e) {
                    System.err.println("Error processing push event: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        // Return 200 OK to acknowledge receipt
        return ResponseEntity.ok("Webhook received at /github");
    }

    /**
     * Handle webhooks sent to the legacy path /api/webhook/github
     */
    @PostMapping("/api/webhook/github")
    public ResponseEntity<String> handleLegacyWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "X-GitHub-Event", required = false) String event,
            @RequestHeader(value = "X-Hub-Signature-256", required = false) String signature) {

        System.out.println("==== WEBHOOK RECEIVED AT LEGACY PATH (/api/webhook/github) ====");
        System.out.println("Event: " + event);
        System.out.println("Signature: " + (signature != null ? "Present" : "Not present"));
        System.out.println("Payload length: " + (payload != null ? payload.length() : 0));
        System.out.println("Payload preview: " + (payload != null && payload.length() > 0 ?
                payload.substring(0, Math.min(payload.length(), 200)) + "..." : "Empty"));
        System.out.println("========================");

        // Process the event using the GitHubEventListener
        if (event != null) {
            gitHubEventListener.processEvent(payload, event);

            // Process push events and save to database
            if ("push".equals(event)) {
                try {
                    processPushEvent(payload);
                } catch (Exception e) {
                    System.err.println("Error processing push event: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        // Return 200 OK to acknowledge receipt
        return ResponseEntity.ok("Webhook received at legacy path");
    }

    /**
     * Simple test endpoint to verify the server is accessible
     */
    @GetMapping("/github-test")
    public ResponseEntity<String> testGithubWebhook() {
        return ResponseEntity.ok("GitHub webhook endpoint is accessible");
    }

    /**
     * Handle webhooks sent to the root path /
     */
    @PostMapping("/")
    public ResponseEntity<String> handleRootPathWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "X-GitHub-Event", required = false) String event,
            @RequestHeader(value = "X-Hub-Signature-256", required = false) String signature) {

        System.out.println("==== WEBHOOK RECEIVED AT ROOT PATH (/) ====");
        System.out.println("Event: " + event);
        System.out.println("Signature: " + (signature != null ? "Present" : "Not present"));
        System.out.println("Payload length: " + (payload != null ? payload.length() : 0));
        System.out.println("Payload preview: " + (payload != null && payload.length() > 0 ?
                payload.substring(0, Math.min(payload.length(), 200)) + "..." : "Empty"));
        System.out.println("========================");

        // Process the event using the GitHubEventListener
        if (event != null) {
            gitHubEventListener.processEvent(payload, event);

            // Process push events and save to database
            if ("push".equals(event)) {
                try {
                    processPushEvent(payload);
                } catch (Exception e) {
                    System.err.println("Error processing push event: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        // Return 200 OK to acknowledge receipt
        return ResponseEntity.ok("Webhook received at root path");
    }

    /**
     * Process a push event and save files to the database
     */
    private void processPushEvent(String payload) throws Exception {
        JsonNode rootNode = objectMapper.readTree(payload);

        // Extract repository information
        String repoOwner = rootNode.path("repository").path("owner").path("name").asText();
        if (repoOwner.isEmpty()) {
            repoOwner = rootNode.path("repository").path("owner").path("login").asText();
        }
        String repoName = rootNode.path("repository").path("name").asText();
        String ref = rootNode.path("ref").asText();
        String branch = ref.replace("refs/heads/", "");

        // Extract pusher information
        String pusherName = rootNode.path("pusher").path("name").asText();

        // Update repository information in the service
        repositoryInfoService.updateFromWebhook(repoOwner, repoName, branch, pusherName);

        // Get the latest commit
        JsonNode commits = rootNode.path("commits");
        if (commits.isArray() && commits.size() > 0) {
            JsonNode latestCommit = commits.get(commits.size() - 1);
            String commitId = latestCommit.path("id").asText();
            String commitMessage = latestCommit.path("message").asText();

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
                System.out.println("Saving " + modifiedFiles.size() + " files to database from push event");
                pushedFileService.saveFilesFromPushEvent(
                        repoOwner, repoName, commitId, commitMessage,
                        modifiedFiles, githubToken, branch, pusherName
                );
            }
        }
    }

    /**
     * Simple ping endpoint to verify the server is running
     */
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("GitHub webhook server is running!");
    }

    /**
     * Endpoint to manually trigger webhook creation for a specific repository
     */
    @PostMapping("/create-webhook")
    public ResponseEntity<String> createWebhook(
            @RequestParam String owner,
            @RequestParam String repo) {

        try {
            boolean result = webhookService.checkAndCreateWebhookForRepo(owner, repo);

            if (result) {
                return ResponseEntity.ok("Webhook for " + owner + "/" + repo + " created or already exists");
            } else {
                return ResponseEntity.badRequest().body("Failed to create webhook for " + owner + "/" + repo);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error creating webhook: " + e.getMessage());
        }
    }

    /**
     * Endpoint to manually trigger webhook check for all repositories
     */
    @PostMapping("/check-all-webhooks")
    public ResponseEntity<String> checkAllWebhooks() {
        try {
            webhookService.checkAndCreateWebhooks();
            return ResponseEntity.ok("Webhook check triggered for all repositories. Check server logs for details.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error checking webhooks: " + e.getMessage());
        }
    }

    /**
     * Endpoint to simulate a repository creation event
     */
    @PostMapping("/simulate-repo-created")
    public ResponseEntity<String> simulateRepoCreated(
            @RequestParam String owner,
            @RequestParam String repo) {

        try {
            // Create a simple JSON payload that simulates a repository creation event
            String payload = "{"
                    + "\"action\": \"created\","
                    + "\"repository\": {"
                    + "\"name\": \"" + repo + "\","
                    + "\"private\": false,"
                    + "\"owner\": {"
                    + "\"login\": \"" + owner + "\""
                    + "}"
                    + "}"
                    + "}";

            // Process the event
            boolean result = gitHubEventListener.processEvent(payload, "repository");

            if (result) {
                return ResponseEntity.ok("Simulated repository creation event processed successfully");
            } else {
                return ResponseEntity.badRequest().body("Failed to process simulated repository creation event");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error simulating repository creation: " + e.getMessage());
        }
    }
}
