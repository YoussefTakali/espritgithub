package esprit.git_push_test.Controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import esprit.git_push_test.Service.RateLimitService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class RepositoryController {

    @Value("${github.token}")
    private String githubToken;

    private final WebClient webClient;
    private final RateLimitService rateLimitService;

    // Constructor Injection of WebClient and RateLimitService
    public RepositoryController(WebClient.Builder webClientBuilder, RateLimitService rateLimitService) {
        this.webClient = webClientBuilder.baseUrl("https://api.github.com").build();
        this.rateLimitService = rateLimitService;
    }

    // ADD THIS NEW ENDPOINT FOR COMMIT DETAILS
    @GetMapping("/repos/{owner}/{repo}/commits/{sha}")
    public ResponseEntity<JsonNode> getCommitDetails(
            @PathVariable String owner,
            @PathVariable String repo,
            @PathVariable String sha) {

        System.out.println("Fetching commit details for: " + owner + "/" + repo + ", SHA: " + sha);

        try {
            // Check rate limit before making request
            if (!rateLimitService.canMakeRequest("commit-details")) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rateLimitResponse = mapper.createObjectNode()
                        .put("error", "Rate limit exceeded")
                        .put("message", "GitHub API rate limit exceeded. Please try again later.")
                        .put("retry_after", "3600");
                return ResponseEntity.status(429).body(rateLimitResponse);
            }

            String url = String.format("/repos/%s/%s/commits/%s", owner, repo, sha);
            System.out.println("Making GitHub API request to: " + url);

            String json = webClient.get()
                    .uri(url)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + githubToken)
                    .header(HttpHeaders.ACCEPT, "application/vnd.github+json")
                    .header("User-Agent", "GitHubStatsApp")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode responseJson = mapper.readTree(json);

            return ResponseEntity.ok(responseJson);

        } catch (WebClientResponseException.NotFound e) {
            System.err.println("404 Not Found - Commit not found: " + e.getMessage());
            ObjectMapper mapper = new ObjectMapper();
            JsonNode errorResponse = mapper.createObjectNode()
                    .put("error", "Commit not found")
                    .put("message", "The requested commit SHA does not exist in this repository.")
                    .put("status", 404);
            return ResponseEntity.status(404).body(errorResponse);

        } catch (WebClientResponseException.Forbidden e) {
            System.err.println("403 Forbidden - Rate limit or access denied: " + e.getMessage());
            ObjectMapper mapper = new ObjectMapper();
            JsonNode errorResponse = mapper.createObjectNode()
                    .put("error", "Access denied or rate limit exceeded")
                    .put("message", "GitHub API returned 403 Forbidden. This usually means rate limit exceeded or repository access denied.")
                    .put("status", 403)
                    .put("retry_after", "3600");
            return ResponseEntity.status(403).body(errorResponse);

        } catch (WebClientResponseException e) {
            System.err.println("GitHub API error: " + e.getStatusCode() + " - " + e.getMessage());
            ObjectMapper mapper = new ObjectMapper();
            JsonNode errorResponse = mapper.createObjectNode()
                    .put("error", "GitHub API error")
                    .put("message", "GitHub API returned: " + e.getStatusCode() + " - " + e.getMessage())
                    .put("status", e.getStatusCode().value());
            return ResponseEntity.status(e.getStatusCode()).body(errorResponse);

        } catch (Exception e) {
            System.err.println("Error fetching commit details: " + e.getMessage());
            e.printStackTrace();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode errorResponse = mapper.createObjectNode()
                    .put("error", "Error fetching commit details")
                    .put("message", e.getMessage())
                    .put("status", 500);

            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/contents")
    public ResponseEntity<JsonNode> getRepoContents(@RequestParam String owner,
                                                    @RequestParam String repo,
                                                    @RequestParam(required = false) String path,
                                                    @RequestParam(required = false, defaultValue = "main") String branch) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            System.out.println("Fetching contents for: " + owner + "/" + repo +
                    ", branch: " + branch +
                    ", path: " + (path == null ? "root" : path));

            // Check rate limit before making request
            if (!rateLimitService.canMakeRequest("contents")) {
                System.out.println("Rate limit exceeded, returning cached/fallback response");
                JsonNode rateLimitResponse = mapper.createObjectNode()
                        .put("error", "Rate limit exceeded")
                        .put("message", "GitHub API rate limit exceeded. Please try again later.")
                        .put("retry_after", "3600"); // 1 hour
                return ResponseEntity.status(429).body(rateLimitResponse);
            }

            // Encoding the path to handle special characters
            String encodedPath = path == null ? "" : path;

            // Building the GitHub API URL - Note how the path parameter is handled
            String url;
            if (encodedPath.isEmpty()) {
                url = String.format("/repos/%s/%s/contents?ref=%s", owner, repo, branch);
            } else {
                url = String.format("/repos/%s/%s/contents/%s?ref=%s", owner, repo, encodedPath, branch);
            }

            System.out.println("Making GitHub API request to: " + url);

            // Fetching the repository contents
            String json = this.webClient.get()
                    .uri(url)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + githubToken)
                    .header(HttpHeaders.ACCEPT, "application/vnd.github+json")
                    .header("User-Agent", "GitHubStatsApp")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(); // Synchronously block for the response

            // Parsing the JSON response
            JsonNode responseJson = mapper.readTree(json);

            return ResponseEntity.ok(responseJson);

        } catch (WebClientResponseException.Forbidden e) {
            System.err.println("403 Forbidden - Rate limit or access denied: " + e.getMessage());

            JsonNode errorResponse = mapper.createObjectNode()
                    .put("error", "Access denied or rate limit exceeded")
                    .put("message", "GitHub API returned 403 Forbidden. This usually means rate limit exceeded or repository access denied.")
                    .put("status", 403)
                    .put("retry_after", "3600");

            return ResponseEntity.status(403).body(errorResponse);

        } catch (WebClientResponseException.NotFound e) {
            System.err.println("404 Not Found - Repository or path not found: " + e.getMessage());

            JsonNode errorResponse = mapper.createObjectNode()
                    .put("error", "Repository or path not found")
                    .put("message", "The requested repository or path does not exist.")
                    .put("status", 404);

            return ResponseEntity.status(404).body(errorResponse);

        } catch (WebClientResponseException e) {
            System.err.println("GitHub API error: " + e.getStatusCode() + " - " + e.getMessage());

            JsonNode errorResponse = mapper.createObjectNode()
                    .put("error", "GitHub API error")
                    .put("message", "GitHub API returned: " + e.getStatusCode() + " - " + e.getMessage())
                    .put("status", e.getStatusCode().value());

            return ResponseEntity.status(e.getStatusCode()).body(errorResponse);

        } catch (Exception e) {
            System.err.println("Error fetching contents: " + e.getMessage());
            e.printStackTrace();

            // Enhanced error handling - Create a JSON response with the error message
            JsonNode errorResponse = mapper.createObjectNode()
                    .put("error", "Error fetching contents")
                    .put("message", e.getMessage())
                    .put("status", 500);

            // Return the error response as a JsonNode
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/repos/{owner}/{repo}/branches")
    public ResponseEntity<List<String>> getRepoBranches(@PathVariable String owner, @PathVariable String repo) {
        try {
            String url = String.format("/repos/%s/%s/branches", owner, repo);

            String json = this.webClient.get()
                    .uri(url)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + githubToken)
                    .header(HttpHeaders.ACCEPT, "application/vnd.github+json")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // Parse JSON and extract branch names
            ObjectMapper mapper = new ObjectMapper();
            JsonNode responseJson = mapper.readTree(json);

            List<String> branchNames = new ArrayList<>();
            for (JsonNode branch : responseJson) {
                branchNames.add(branch.get("name").asText());
            }

            return ResponseEntity.ok(branchNames);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(List.of("Error fetching branches: " + e.getMessage()));
        }
    }

    @GetMapping("/repos/{owner}/{repo}/commits")
    public ResponseEntity<JsonNode> getRepoCommits(
            @PathVariable String owner,
            @PathVariable String repo,
            @RequestParam(required = false) String branch) {

        System.out.println("Fetching commits for: " + owner + "/" + repo +
                (branch != null ? ", branch: " + branch : ", default branch"));

        try {
            // Only include the sha parameter if branch is provided
            String url;
            if (branch != null && !branch.isEmpty()) {
                url = String.format("/repos/%s/%s/commits?sha=%s", owner, repo, branch);
            } else {
                url = String.format("/repos/%s/%s/commits", owner, repo);
            }

            System.out.println("Making GitHub API request to: " + url);

            String json = webClient.get()
                    .uri(url)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + githubToken)
                    .header(HttpHeaders.ACCEPT, "application/vnd.github+json")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode responseJson = mapper.readTree(json);

            return ResponseEntity.ok(responseJson);
        } catch (Exception e) {
            System.err.println("Error fetching commits: " + e.getMessage());
            e.printStackTrace();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode errorResponse = mapper.createObjectNode()
                    .put("error", "Error fetching commits: " + e.getMessage());

            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Check current rate limit status
     */
    @GetMapping("/rate-limit-status")
    public ResponseEntity<JsonNode> getRateLimitStatus() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            RateLimitService.RateLimitStatus status = rateLimitService.getRateLimitStatus();

            JsonNode response = mapper.createObjectNode()
                    .put("hourly_requests_used", status.currentHourlyRequests)
                    .put("hourly_requests_limit", status.maxHourlyRequests)
                    .put("hourly_requests_remaining", status.getHourlyRequestsRemaining())
                    .put("minute_requests_used", status.currentMinuteRequests)
                    .put("minute_requests_limit", status.maxMinuteRequests)
                    .put("minute_requests_remaining", status.getMinuteRequestsRemaining())
                    .put("can_make_request", status.canMakeRequest())
                    .put("hourly_reset_time", status.hourlyResetTime.toString())
                    .put("minute_reset_time", status.minuteResetTime.toString());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode errorResponse = mapper.createObjectNode()
                    .put("error", "Error checking rate limit status")
                    .put("message", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Reset rate limits manually (for testing)
     */
    @PostMapping("/reset-rate-limits")
    public ResponseEntity<JsonNode> resetRateLimits() {
        try {
            rateLimitService.resetRateLimits();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode response = mapper.createObjectNode()
                    .put("message", "Rate limits have been reset")
                    .put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode errorResponse = mapper.createObjectNode()
                    .put("error", "Error resetting rate limits")
                    .put("message", e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Get repository contents at a specific commit
     * This allows browsing the repository state at any point in history
     */
    @GetMapping("/repos/{owner}/{repo}/contents-at-commit")
    public ResponseEntity<JsonNode> getContentsAtCommit(
            @PathVariable String owner,
            @PathVariable String repo,
            @RequestParam String sha,
            @RequestParam(required = false) String path) {

        System.out.println("Fetching contents at commit for: " + owner + "/" + repo +
                ", SHA: " + sha + ", path: " + (path == null ? "root" : path));

        try {
            // Check rate limit before making request
            if (!rateLimitService.canMakeRequest("contents-at-commit")) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rateLimitResponse = mapper.createObjectNode()
                        .put("error", "Rate limit exceeded")
                        .put("message", "GitHub API rate limit exceeded. Please try again later.")
                        .put("retry_after", "3600");
                return ResponseEntity.status(429).body(rateLimitResponse);
            }

            // Build URL for contents at specific commit
            String encodedPath = path == null ? "" : path;
            String url;
            if (encodedPath.isEmpty()) {
                url = String.format("/repos/%s/%s/contents?ref=%s", owner, repo, sha);
            } else {
                url = String.format("/repos/%s/%s/contents/%s?ref=%s", owner, repo, encodedPath, sha);
            }

            System.out.println("Making GitHub API request to: " + url);

            String json = webClient.get()
                    .uri(url)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + githubToken)
                    .header(HttpHeaders.ACCEPT, "application/vnd.github+json")
                    .header("User-Agent", "GitHubStatsApp")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode contents = mapper.readTree(json);

            // Enhance response with commit information
            JsonNode enhancedResponse = mapper.createObjectNode()
                    .put("commit_sha", sha)
                    .put("path", path == null ? "" : path)
                    .set("contents", contents);

            return ResponseEntity.ok(enhancedResponse);

        } catch (WebClientResponseException.NotFound e) {
            System.err.println("404 Not Found - Commit or path not found: " + e.getMessage());
            ObjectMapper mapper = new ObjectMapper();
            JsonNode errorResponse = mapper.createObjectNode()
                    .put("error", "Commit or path not found")
                    .put("message", "The requested commit SHA or path does not exist.")
                    .put("status", 404);
            return ResponseEntity.status(404).body(errorResponse);

        } catch (WebClientResponseException e) {
            System.err.println("GitHub API error: " + e.getStatusCode() + " - " + e.getMessage());
            ObjectMapper mapper = new ObjectMapper();
            JsonNode errorResponse = mapper.createObjectNode()
                    .put("error", "GitHub API error")
                    .put("message", "GitHub API returned: " + e.getStatusCode() + " - " + e.getMessage())
                    .put("status", e.getStatusCode().value());
            return ResponseEntity.status(e.getStatusCode()).body(errorResponse);

        } catch (Exception e) {
            System.err.println("Error fetching contents at commit: " + e.getMessage());
            e.printStackTrace();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode errorResponse = mapper.createObjectNode()
                    .put("error", "Error fetching contents at commit")
                    .put("message", e.getMessage())
                    .put("status", 500);

            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Compare two commits to see the differences
     * This is useful for showing what changed between versions
     */
    @GetMapping("/repos/{owner}/{repo}/compare/{base}...{head}")
    public ResponseEntity<JsonNode> compareCommits(
            @PathVariable String owner,
            @PathVariable String repo,
            @PathVariable String base,
            @PathVariable String head) {

        System.out.println("Comparing commits for: " + owner + "/" + repo +
                ", base: " + base + ", head: " + head);

        try {
            // Check rate limit before making request
            if (!rateLimitService.canMakeRequest("compare-commits")) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rateLimitResponse = mapper.createObjectNode()
                        .put("error", "Rate limit exceeded")
                        .put("message", "GitHub API rate limit exceeded. Please try again later.")
                        .put("retry_after", "3600");
                return ResponseEntity.status(429).body(rateLimitResponse);
            }

            String url = String.format("/repos/%s/%s/compare/%s...%s", owner, repo, base, head);
            System.out.println("Making GitHub API request to: " + url);

            String json = webClient.get()
                    .uri(url)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + githubToken)
                    .header(HttpHeaders.ACCEPT, "application/vnd.github+json")
                    .header("User-Agent", "GitHubStatsApp")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode responseJson = mapper.readTree(json);

            return ResponseEntity.ok(responseJson);

        } catch (WebClientResponseException e) {
            System.err.println("GitHub API error: " + e.getStatusCode() + " - " + e.getMessage());
            ObjectMapper mapper = new ObjectMapper();
            JsonNode errorResponse = mapper.createObjectNode()
                    .put("error", "GitHub API error")
                    .put("message", "GitHub API returned: " + e.getStatusCode() + " - " + e.getMessage())
                    .put("status", e.getStatusCode().value());
            return ResponseEntity.status(e.getStatusCode()).body(errorResponse);

        } catch (Exception e) {
            System.err.println("Error comparing commits: " + e.getMessage());
            e.printStackTrace();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode errorResponse = mapper.createObjectNode()
                    .put("error", "Error comparing commits")
                    .put("message", e.getMessage())
                    .put("status", 500);

            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    @GetMapping("/repos")
    public ResponseEntity<JsonNode> getAuthenticatedUserRepos() {
        ObjectMapper mapper = new ObjectMapper();

        try {
            // Check rate limit before making request
            if (!rateLimitService.canMakeRequest("user-repos")) {
                System.out.println("Rate limit exceeded for user repos request");
                JsonNode rateLimitResponse = mapper.createObjectNode()
                        .put("error", "Rate limit exceeded")
                        .put("message", "GitHub API rate limit exceeded. Please try again later.")
                        .put("retry_after", "3600");
                return ResponseEntity.status(429).body(rateLimitResponse);
            }

            String url = "/user/repos";
            System.out.println("Making GitHub API request to: " + url);

            String json = this.webClient.get()
                    .uri(url)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + githubToken)
                    .header(HttpHeaders.ACCEPT, "application/vnd.github+json")
                    .header("User-Agent", "GitHubStatsApp")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode responseJson = mapper.readTree(json);
            return ResponseEntity.ok(responseJson);

        } catch (WebClientResponseException.Forbidden e) {
            System.err.println("403 Forbidden - Rate limit exceeded: " + e.getMessage());

            // Check if it's specifically a rate limit error
            String responseBody = e.getResponseBodyAsString();
            if (responseBody.contains("rate limit exceeded")) {
                JsonNode errorResponse = mapper.createObjectNode()
                        .put("error", "GitHub API rate limit exceeded")
                        .put("message", "You have exceeded the GitHub API rate limit. Please wait before making more requests.")
                        .put("status", 403)
                        .put("retry_after", "3600")
                        .put("documentation", "https://docs.github.com/rest/overview/rate-limits-for-the-rest-api");
                return ResponseEntity.status(429).body(errorResponse); // Use 429 for rate limiting
            }

            // Generic 403 error
            JsonNode errorResponse = mapper.createObjectNode()
                    .put("error", "Access denied")
                    .put("message", "GitHub API returned 403 Forbidden. Check your token permissions.")
                    .put("status", 403);
            return ResponseEntity.status(403).body(errorResponse);

        } catch (WebClientResponseException e) {
            System.err.println("GitHub API error: " + e.getStatusCode() + " - " + e.getMessage());
            JsonNode errorResponse = mapper.createObjectNode()
                    .put("error", "GitHub API error")
                    .put("message", "GitHub API returned: " + e.getStatusCode() + " - " + e.getMessage())
                    .put("status", e.getStatusCode().value());
            return ResponseEntity.status(e.getStatusCode()).body(errorResponse);

        } catch (Exception e) {
            System.err.println("Error fetching repos: " + e.getMessage());
            e.printStackTrace();

            JsonNode errorResponse = mapper.createObjectNode()
                    .put("error", "Error fetching repos")
                    .put("message", e.getMessage())
                    .put("status", 500);

            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}