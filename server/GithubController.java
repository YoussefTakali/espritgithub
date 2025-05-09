package com.example.gitbackend.controllers;
import com.example.gitbackend.entities.AddCollaboratorRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import  com.example.gitbackend.entities.CreateRepoRequest;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/github")
public class GithubController {

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
                .bodyToMono(String.class);
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

        public GithubRepoPayload(CreateRepoRequest req) {
            this.name = req.getName();
            this.description = req.getDescription();
            this.homepage = "https://github.com";
            this.privateRepo = req.isPrivate(); // Make sure this is a boolean
            this.is_template = true;
            this.auto_init = req.isAuto_init(); // <-- now correct
            this.gitignore_template = req.getGitignore_template(); // <-- now correct
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
}