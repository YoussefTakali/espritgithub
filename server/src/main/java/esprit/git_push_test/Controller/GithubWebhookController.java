package esprit.git_push_test.Controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@RestController
@RequestMapping("/github")
public class GithubWebhookController {

    @Value("${github.token}")
    private String githubToken;

    @PostMapping("/register-webhook")
    public ResponseEntity<String> registerWebhook(
            @RequestParam String owner,
            @RequestParam String repo,
            @RequestParam String webhookUrl,
            @RequestParam(required = false) String secret) {

        String url = "https://api.github.com/repos/" + owner + "/" + repo + "/hooks";

        Map<String, Object> config = new HashMap<>();
        config.put("url", webhookUrl);
        config.put("content_type", "json");
        if (secret != null && !secret.isEmpty()) {
            config.put("secret", secret);
        }

        Map<String, Object> body = new HashMap<>();
        body.put("name", "web");
        body.put("active", true);
        body.put("events", Arrays.asList("push"));
        body.put("config", config);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(githubToken);
        headers.set("Accept", "application/vnd.github+json");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }
} 