package com.example.gitbackend.controllers;
import com.example.gitbackend.entities.AddCollaboratorRequest;
import com.example.gitbackend.entities.Commit;
import com.example.gitbackend.entities.RepositoryEntity;
import com.example.gitbackend.repos.CommitRepository;
import com.example.gitbackend.repos.repositoryRepository;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import  com.example.gitbackend.entities.CreateRepoRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.aspectj.weaver.tools.cache.SimpleCacheFactory.path;

@Slf4j


@RestController
@RequestMapping("/api/github")
public class GithubController {
    @Autowired
    private CommitRepository commitRepository;
    @Autowired
    private repositoryRepository repo;
    private final WebClient webClient;

    // Inject your GitHub token here (or get it from a service)
    @Value("${github.token}")
    private String githubToken;

    public GithubController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.github.com").build();
    }

    @PostMapping("/create-repo")
    public Mono<String> createRepo(@RequestBody CreateRepoRequest request) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            // For debugging: print the payload being sent to GitHub
            System.out.println(mapper.writeValueAsString(new GithubRepoPayload(request)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return webClient.post()
                .uri("/user/repos")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + githubToken)
                .header("Accept", "application/vnd.github+json")
                .header("X-GitHub-Api-Version", "2022-11-28")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new GithubRepoPayload(request))
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> {
                    // Save repo info to DB
                    RepositoryEntity repoEntity = new RepositoryEntity();
                    repoEntity.setRepoName(request.getName());
                    repoEntity.setOwnerName(request.getOwnerName());
                    repoEntity.setCreationDate(new Date());
                    repo.save(repoEntity);
                    try {
                        Thread.sleep(10000); // 3 seconds
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Register webhook for the new repo
                    String webhookUrl = "https://sweet-rocks-look.loca.lt/api/github/webhook";// <-- Replace with your tunnel or prod URL
                    log.info(webhookUrl);
                    registerWebhookForRepo(request.getOwnerName(), request.getName(), webhookUrl, githubToken);
                });
    }

    // Place this OUTSIDE the createRepo method!
    public void registerWebhookForRepo(String owner, String repo, String webhookUrl, String githubToken) {
        String url = "https://api.github.com/repos/" + owner + "/" + repo + "/hooks";

        Map<String, Object> config = new HashMap<>();
        config.put("url", webhookUrl);
        config.put("content_type", "json");
        // Optionally: config.put("secret", "your-secret");

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

        // Optionally, check response.getStatusCode() and handle errors
    }

    // Helper class to map to GitHub's expected payload
    public class GithubRepoPayload {
        public String name;
        public String description;
        public String homepage;
        @JsonProperty("private")
        public boolean privateRepo;
        public boolean is_template;
        public Boolean auto_init; // for README
        public String gitignore_template; // for .gitignore

        // This field is for your own use, not sent to GitHub
        public String ownerName;

        public GithubRepoPayload(CreateRepoRequest req) {
            this.name = req.getName();
            this.description = req.getDescription();
            this.homepage = "https://github.com";
            this.privateRepo = req.isPrivate();
            this.is_template = true;
            this.auto_init = req.isAuto_init();
            this.gitignore_template = req.getGitignore_template();
            this.ownerName = req.getOwnerName(); // For your own use
        }
    }
    @PostMapping("/add-collaborator")
    public Mono<String> addCollaborator(@RequestBody AddCollaboratorRequest request) {
        String url = String.format("/repos/%s/%s/collaborators/%s",
                request.getOwner(), request.getRepo(), request.getUsername());

        Map<String, String> body = new HashMap<>();
        if (request.getPermission() != null && !request.getPermission().isEmpty()) {
            body.put("permission", request.getPermission());
        }

        return webClient.put()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + githubToken)
                .header("Accept", "application/vnd.github+json")
                .header("X-GitHub-Api-Version", "2022-11-28")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class);
    }

    @GetMapping("/list-collaborators")
    public Mono<String> listCollaborators(
            @RequestParam String owner,
            @RequestParam String repo
    ) {
        String url = String.format("/repos/%s/%s/collaborators", owner, repo);

        return webClient.get()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + githubToken)
                .header("Accept", "application/vnd.github+json")
                .header("X-GitHub-Api-Version", "2022-11-28")
                .retrieve()
                .bodyToMono(String.class);
    }




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




    @GetMapping("/list-invitations")
    public Mono<String> listInvitations(
            @RequestParam String owner,
            @RequestParam String repo
    ) {
        String url = String.format("/repos/%s/%s/invitations", owner, repo);

        return webClient.get()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + githubToken)
                .header("Accept", "application/vnd.github+json")
                .header("X-GitHub-Api-Version", "2022-11-28")
                .retrieve()
                .bodyToMono(String.class);
    }

    @DeleteMapping("/remove-collaborator")
    public Mono<String> removeCollaborator(
            @RequestParam String owner,
            @RequestParam String repo,
            @RequestParam String username
    ) {
        String url = String.format("/repos/%s/%s/collaborators/%s", owner, repo, username);

        return webClient.delete()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + githubToken)
                .header("Accept", "application/vnd.github+json")
                .header("X-GitHub-Api-Version", "2022-11-28")
                .retrieve()
                .bodyToMono(String.class);
    }
    @DeleteMapping("/remove-invitation")
    public Mono<String> removeInvitation(
            @RequestParam String owner,
            @RequestParam String repo,
            @RequestParam String invitationId
    ) {
        String url = String.format("/repos/%s/%s/invitations/%s", owner, repo, invitationId);

        return webClient.delete()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + githubToken)
                .header("Accept", "application/vnd.github+json")
                .header("X-GitHub-Api-Version", "2022-11-28")
                .retrieve()
                .bodyToMono(String.class);
    }
    // 1. Get latest commit SHA
    @GetMapping("/latest-sha")
    public Mono<String> getLatestCommitSha(String owner, String repo, String baseBranch) {
        String url = String.format("/repos/%s/%s/git/ref/heads/%s", owner, repo, baseBranch);
        return webClient.get()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + githubToken)
                .header("Accept", "application/vnd.github+json")
                .retrieve()
                .bodyToMono(String.class);
    }

    // 2. Create new branch
    @PostMapping("/create-branch")
    public Mono<String> createBranch(
            @RequestParam String owner,
            @RequestParam String repo,
            @RequestParam String newBranch,
            @RequestParam String sha) {
        String url = String.format("/repos/%s/%s/git/refs", owner, repo);
        Map<String, String> body = new HashMap<>();
        body.put("ref", "refs/heads/" + newBranch);
        body.put("sha", sha);
        return webClient.post()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + githubToken)
                .header("Accept", "application/vnd.github+json")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class);
    }
    @GetMapping("/list-branches")
    public Mono<String> listBranches(
            @RequestParam String owner,
            @RequestParam String repo
    ) {
        try {
            System.out.println("getting branches for " + owner + " " + repo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String url = String.format("/repos/%s/%s/branches", owner, repo);

        return webClient.get()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + githubToken)
                .header("Accept", "application/vnd.github+json")
                .header("X-GitHub-Api-Version", "2022-11-28")
                .retrieve()
                .bodyToMono(String.class);
    }
    @GetMapping("/branch-exists")
    public Mono<Boolean> branchExists(
            @RequestParam String owner,
            @RequestParam String repo,
            @RequestParam String branch
    ) {
        String url = String.format("/repos/%s/%s/branches/%s", owner, repo, branch);

        return webClient.get()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + githubToken)
                .header("Accept", "application/vnd.github+json")
                .header("X-GitHub-Api-Version", "2022-11-28")
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return Mono.just(true);
                    } else if (response.statusCode().value() == 404) {
                        return Mono.just(false);
                    } else {
                        return response.createException().flatMap(Mono::error);
                    }
                });
    }


    @DeleteMapping("/delete-branch")
    public Mono<String> deleteBranch(
            @RequestParam String owner,
            @RequestParam String repo,
            @RequestParam String branch
    ) {
        String url = String.format("/repos/%s/%s/git/refs/heads/%s", owner, repo, branch);
        return webClient.delete()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + githubToken)
                .header("Accept", "application/vnd.github+json")
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> Mono.just("Error deleting branch: " + e.getMessage()));
    }

    @GetMapping("/branch-commits")
    public Mono<String> getBranchCommits(
            @RequestParam String owner,
            @RequestParam String repo,
            @RequestParam String branch
    ) {
        String url = String.format("/repos/%s/%s/commits?sha=%s", owner, repo, branch);

        return webClient.get()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + githubToken)
                .header("Accept", "application/vnd.github+json")
                .retrieve()
                .bodyToMono(String.class);
    }


    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody Map<String, Object> payload) {
        try {
            // 1. Parse repo info
            Map<String, Object> repoMap = (Map<String, Object>) payload.get("repository");
            String repoName = (String) repoMap.get("name");
            Map<String, Object> ownerMap = (Map<String, Object>) repoMap.get("owner");
            String ownerName = (String) ownerMap.get("name"); // or "login" if using GitHub usernames


            // 2. Find or create the repository in your DB
            RepositoryEntity repoEntity = repo.findByRepoNameAndOwnerName(repoName, ownerName);
            if (repoEntity == null) {
                repoEntity = new RepositoryEntity();
                repoEntity.setRepoName(repoName);
                repoEntity.setOwnerName(ownerName);
                repoEntity.setCreationDate(new Date());
                repo.save(repoEntity);
            }

            // 3. Get branch name
            String ref = (String) payload.get("ref"); // e.g., "refs/heads/main"
            String branch = ref != null ? ref.replace("refs/heads/", "") : null;

            // 4. Save each commit
            List<Map<String, Object>> commits = (List<Map<String, Object>>) payload.get("commits");
            for (Map<String, Object> commit : commits) {
                Commit entity = new Commit();
                entity.setSha((String) commit.get("id"));
                entity.setBranch(branch);
                entity.setMessage((String) commit.get("message"));
                entity.setDate((String) commit.get("timestamp"));

                Map<String, Object> author = (Map<String, Object>) commit.get("author");
                if (author != null) {
                    entity.setAuthorName((String) author.get("name"));
                    entity.setAuthorLogin((String) author.get("username"));
                }
                entity.setRepository(repoEntity); // Link to repo

                commitRepository.save(entity);
            }

            return ResponseEntity.ok("Commits saved from webhook");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error processing webhook");
        }
    }

    @GetMapping("/user-repos")
    public Mono<String> getUserRepos(@RequestParam String username) {
        String url = String.format("/users/%s/repos?sort=created&direction=desc", username);
        return webClient.get()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + githubToken)
                .header("Accept", "application/vnd.github+json")
                .retrieve()
                .bodyToMono(String.class);
    }
    @GetMapping("/repo-contents")
    public ResponseEntity<?> getRepoContents(@RequestParam String owner,
                                             @RequestParam String repo,
                                             @RequestParam(required = false) String path,
                                             @RequestParam(required = false, defaultValue = "main") String branch) {
        try {
            String encodedPath = path == null ? "" : path;
            String url = String.format("https://api.github.com/repos/%s/%s/contents/%s", owner, repo, encodedPath);

            if (branch != null && !branch.isEmpty()) {
                url += "?ref=" + branch;
            }

            WebClient client = WebClient.builder()
                    .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)) // 10MB
                    .build();

            // Use byte[] to avoid memory leaks and support large payloads (binary-safe)
            byte[] responseBytes = client.get()
                    .uri(url)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + githubToken)
                    .header(HttpHeaders.ACCEPT, "application/vnd.github+json")
                    .retrieve()
                    .bodyToMono(byte[].class) // ✅ this avoids ByteBuf leaks
                    .block();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(responseBytes);

            return ResponseEntity.ok(json);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching contents: " + e.getMessage() +
                    " for path: " + path + " in repo: " + owner + "/" + repo);
        }
    }

    @GetMapping("/latest-commit")
    public ResponseEntity<?> getLatestCommit(
            @RequestParam String owner,
            @RequestParam String repo,
            @RequestParam String path,
            @RequestParam String branch) {

        try {
            // Build the URL properly with encoding
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl("https://api.github.com/repos/" + owner + "/" + repo + "/commits")
                    .queryParam("path", path)
                    .queryParam("sha", branch)
                    .queryParam("per_page", 1);

            String url = builder.build().encode().toUriString();

            // Log the URL for debugging
            System.out.println("Latest commit URL: " + url);

            WebClient client = WebClient.create();
            String json = client.get()
                    .uri(url)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + githubToken)
                    .header(HttpHeaders.ACCEPT, "application/vnd.github+json")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            ObjectMapper mapper = new ObjectMapper();
            return ResponseEntity.ok(mapper.readTree(json));

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching commit: " + e.getMessage() +
                    " for path: " + path + " in repo: " + owner + "/" + repo + ", branch: " + branch);
        }
    }

    @GetMapping("/latest-repo-commit")
    public ResponseEntity<?> getLatestRepoCommit(
            @RequestParam String owner,
            @RequestParam String repo,
            @RequestParam String branch) {

        try {
            // Build the URL properly with encoding
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl("https://api.github.com/repos/" + owner + "/" + repo + "/commits")
                    .queryParam("sha", branch)
                    .queryParam("per_page", 1);

            String url = builder.build().encode().toUriString();

            // Log the URL for debugging
            System.out.println("Latest repo commit URL: " + url);

            WebClient client = WebClient.create();
            String json = client.get()
                    .uri(url)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + githubToken)
                    .header(HttpHeaders.ACCEPT, "application/vnd.github+json")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            ObjectMapper mapper = new ObjectMapper();
            return ResponseEntity.ok(mapper.readTree(json));

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching latest repo commit: " + e.getMessage() +
                    " for repo: " + owner + "/" + repo + ", branch: " + branch);
        }
    }
    @GetMapping("/commit-count")
    public ResponseEntity<?> getCommitCount(
            @RequestParam String owner,
            @RequestParam String repo,
            @RequestParam(required = false) String path,
            @RequestParam String branch) {

        try {
            // Build the URL properly with encoding
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl("https://api.github.com/repos/" + owner + "/" + repo + "/commits")
                    .queryParam("sha", branch)
                    .queryParam("per_page", 1);

            // Add path parameter if provided
            if (path != null && !path.isEmpty()) {
                builder.queryParam("path", path);
            }

            String countUrl = builder.build().encode().toUriString();

            // First get the commit count by checking the Link header
            WebClient client = WebClient.create();
            ClientResponse response = client.get()
                    .uri(countUrl)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + githubToken)
                    .header(HttpHeaders.ACCEPT, "application/vnd.github+json")
                    .exchange()
                    .block();

            // Extract the last page number from the Link header if it exists
            int commitCount = 0;
            List<String> linkHeaders = response.headers().asHttpHeaders().get("Link");
            if (linkHeaders != null && !linkHeaders.isEmpty()) {
                String linkHeader = linkHeaders.get(0);
                // Parse the Link header to get the last page number
                Pattern pattern = Pattern.compile("page=(\\d+)>; rel=\"last\"");
                Matcher matcher = pattern.matcher(linkHeader);
                if (matcher.find()) {
                    int lastPage = Integer.parseInt(matcher.group(1));
                    // GitHub API returns max 100 commits per page
                    commitCount = lastPage * 100;
                }
            }

            // If we can't get the count from Link header, we'll get the total count directly
            // This could be expensive for large repos, so use with caution
            if (commitCount == 0) {
                // Get all commits (limited to first 100 for performance)
                UriComponentsBuilder allCommitsBuilder = UriComponentsBuilder
                        .fromHttpUrl("https://api.github.com/repos/" + owner + "/" + repo + "/commits")
                        .queryParam("sha", branch)
                        .queryParam("per_page", 100);

                // Add path parameter if provided
                if (path != null && !path.isEmpty()) {
                    allCommitsBuilder.queryParam("path", path);
                }

                String allCommitsUrl = allCommitsBuilder.build().encode().toUriString();

                String commitsJson = client.get()
                        .uri(allCommitsUrl)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + githubToken)
                        .header(HttpHeaders.ACCEPT, "application/vnd.github+json")
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

                ObjectMapper mapper = new ObjectMapper();
                JsonNode commitsArray = mapper.readTree(commitsJson);
                commitCount = commitsArray.size();
            }

            // Return the count as JSON
            Map<String, Integer> result = new HashMap<>();
            result.put("count", commitCount);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching commit count: " + e.getMessage() +
                    " for path: " + path + " in repo: " + owner + "/" + repo + ", branch: " + branch);
        }
    }}