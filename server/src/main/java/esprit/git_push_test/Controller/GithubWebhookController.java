package esprit.git_push_test.Controller;

import esprit.git_push_test.Service.WebhookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/webhook")  // Dedicated mapping for webhook functionality
public class GithubWebhookController {

    @Value("${github.token}")
    private String githubToken;

    @Value("${webhook.url}")
    private String webhookUrl;

    @Value("${github.webhook.secret:}") // optional, defaults to empty string
    private String secret;

    @Autowired
    private WebhookService webhookService;

    @PostMapping("/register-all")
    public ResponseEntity<String> registerWebhookForAllRepos() {
        try {
            // Manually trigger the webhook service to check and create webhooks
            webhookService.checkAndCreateWebhooks();

            return ResponseEntity.ok("Webhook registration process started. Check server logs for details.");
        } catch (Exception e) {
            System.err.println("Error triggering webhook registration: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("Failed to register webhooks: " + e.getMessage());
        }
    }

    @PostMapping("/github")  // Endpoint for GitHub webhook callbacks
    public ResponseEntity<String> handleGithubWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "X-GitHub-Event", required = false) String event,
            @RequestHeader(value = "X-Hub-Signature-256", required = false) String signature) {

        // Log the incoming webhook with detailed information
        logWebhookDetails("/webhook/github", event, payload, signature);

        // Process the webhook payload here
        // You might want to parse the JSON and take different actions based on event type

        // Return 200 OK to acknowledge receipt
        return ResponseEntity.ok("Webhook received");
    }

    // Add a more detailed logging method to help diagnose webhook issues
    private void logWebhookDetails(String path, String event, String payload, String signature) {
        System.out.println("==== WEBHOOK RECEIVED ====");
        System.out.println("Path: " + path);
        System.out.println("Event: " + event);
        System.out.println("Signature: " + (signature != null ? "Present" : "Not present"));
        System.out.println("Payload length: " + (payload != null ? payload.length() : 0));
        System.out.println("Payload preview: " + (payload != null && payload.length() > 0 ?
                payload.substring(0, Math.min(payload.length(), 200)) + "..." : "Empty"));
        System.out.println("========================");
    }

    @GetMapping("/test")
    public ResponseEntity<String> testWebhook() {
        // Return information about the configured webhook URL
        return ResponseEntity.ok("Webhook URL configured as: " + webhookUrl +
                "\nTo register webhooks for all repositories, use: POST /webhook/register-all" +
                "\nThis will create webhooks for all PUBLIC repositories that your GitHub token has access to." +
                "\nPrivate repositories will be automatically skipped.");
    }
}