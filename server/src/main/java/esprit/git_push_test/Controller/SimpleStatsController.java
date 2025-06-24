package esprit.git_push_test.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/simple-stats")
public class SimpleStatsController {

    /**
     * Test endpoint to verify the controller is working
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testEndpoint() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "SimpleStatsController is working!");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    /**
     * Mock language statistics
     */
    @GetMapping("/{owner}/{repo}/languages")
    public ResponseEntity<Map<String, Object>> getLanguageStats(
            @PathVariable String owner,
            @PathVariable String repo) {
        
        Map<String, Object> languages = new HashMap<>();
        
        // Mock data
        Map<String, Object> javaInfo = new HashMap<>();
        javaInfo.put("bytes", 15420);
        javaInfo.put("percentage", 45.2);
        languages.put("Java", javaInfo);
        
        Map<String, Object> jsInfo = new HashMap<>();
        jsInfo.put("bytes", 8930);
        jsInfo.put("percentage", 26.1);
        languages.put("JavaScript", jsInfo);
        
        Map<String, Object> htmlInfo = new HashMap<>();
        htmlInfo.put("bytes", 5680);
        htmlInfo.put("percentage", 16.6);
        languages.put("HTML", htmlInfo);
        
        Map<String, Object> cssInfo = new HashMap<>();
        cssInfo.put("bytes", 4120);
        cssInfo.put("percentage", 12.1);
        languages.put("CSS", cssInfo);
        
        return ResponseEntity.ok(languages);
    }

    /**
     * Mock activity statistics
     */
    @GetMapping("/{owner}/{repo}/activity")
    public ResponseEntity<Map<String, Object>> getCommitActivity(
            @PathVariable String owner,
            @PathVariable String repo,
            @RequestParam(required = false, defaultValue = "30") int days) {
        
        Map<String, Object> activity = new HashMap<>();
        activity.put("totalCommits", 25);
        activity.put("averageCommitsPerDay", 0.83);
        
        Map<String, Integer> commitsPerDay = new HashMap<>();
        commitsPerDay.put("2025-01-15", 3);
        commitsPerDay.put("2025-01-14", 1);
        commitsPerDay.put("2025-01-13", 2);
        commitsPerDay.put("2025-01-12", 0);
        commitsPerDay.put("2025-01-11", 4);
        
        activity.put("commitsPerDay", commitsPerDay);
        
        return ResponseEntity.ok(activity);
    }

    /**
     * Mock file type distribution
     */
    @GetMapping("/{owner}/{repo}/file-types")
    public ResponseEntity<Map<String, Object>> getFileTypeDistribution(
            @PathVariable String owner,
            @PathVariable String repo) {
        
        Map<String, Object> fileTypes = new HashMap<>();
        
        Map<String, Object> javaFiles = new HashMap<>();
        javaFiles.put("count", 12);
        javaFiles.put("percentage", 40.0);
        fileTypes.put("java", javaFiles);
        
        Map<String, Object> jsFiles = new HashMap<>();
        jsFiles.put("count", 8);
        jsFiles.put("percentage", 26.7);
        fileTypes.put("js", jsFiles);
        
        Map<String, Object> htmlFiles = new HashMap<>();
        htmlFiles.put("count", 5);
        htmlFiles.put("percentage", 16.7);
        fileTypes.put("html", htmlFiles);
        
        Map<String, Object> cssFiles = new HashMap<>();
        cssFiles.put("count", 3);
        cssFiles.put("percentage", 10.0);
        fileTypes.put("css", cssFiles);
        
        Map<String, Object> otherFiles = new HashMap<>();
        otherFiles.put("count", 2);
        otherFiles.put("percentage", 6.6);
        fileTypes.put("other", otherFiles);
        
        return ResponseEntity.ok(fileTypes);
    }

    /**
     * Mock contributor statistics
     */
    @GetMapping("/{owner}/{repo}/contributors")
    public ResponseEntity<Map<String, Object>> getContributorStats(
            @PathVariable String owner,
            @PathVariable String repo) {
        
        Map<String, Object> contributors = new HashMap<>();
        contributors.put("totalContributors", 3);
        
        // Mock contributor data
        Map<String, Object> contributor1 = new HashMap<>();
        contributor1.put("login", owner);
        contributor1.put("contributions", 45);
        contributor1.put("avatar_url", "https://github.com/identicons/" + owner + ".png");
        
        Map<String, Object> contributor2 = new HashMap<>();
        contributor2.put("login", "contributor2");
        contributor2.put("contributions", 23);
        contributor2.put("avatar_url", "https://github.com/identicons/contributor2.png");
        
        Map<String, Object> contributor3 = new HashMap<>();
        contributor3.put("login", "contributor3");
        contributor3.put("contributions", 12);
        contributor3.put("avatar_url", "https://github.com/identicons/contributor3.png");
        
        contributors.put("contributors", new Object[]{contributor1, contributor2, contributor3});
        
        return ResponseEntity.ok(contributors);
    }

    /**
     * Mock repository size
     */
    @GetMapping("/{owner}/{repo}/size")
    public ResponseEntity<Map<String, Object>> getRepositorySize(
            @PathVariable String owner,
            @PathVariable String repo) {
        
        Map<String, Object> sizeStats = new HashMap<>();
        sizeStats.put("sizeKB", 2048);
        sizeStats.put("sizeMB", 2.0);
        
        return ResponseEntity.ok(sizeStats);
    }

    /**
     * Mock push frequency
     */
    @GetMapping("/{owner}/{repo}/push-frequency")
    public ResponseEntity<Map<String, Object>> getPushFrequency(
            @PathVariable String owner,
            @PathVariable String repo,
            @RequestParam(required = false, defaultValue = "30") int days) {
        
        Map<String, Object> pushStats = new HashMap<>();
        pushStats.put("totalPushes", 15);
        pushStats.put("averagePushesPerDay", 0.5);
        
        Map<String, Integer> pushesPerDay = new HashMap<>();
        pushesPerDay.put("2025-01-15", 2);
        pushesPerDay.put("2025-01-14", 0);
        pushesPerDay.put("2025-01-13", 1);
        pushesPerDay.put("2025-01-12", 0);
        pushesPerDay.put("2025-01-11", 3);
        
        pushStats.put("pushesPerDay", pushesPerDay);
        
        return ResponseEntity.ok(pushStats);
    }

    /**
     * Mock comprehensive repository statistics
     */
    @GetMapping("/{owner}/{repo}")
    public ResponseEntity<Map<String, Object>> getRepositoryStats(
            @PathVariable String owner,
            @PathVariable String repo,
            @RequestParam(required = false) String branch) {
        
        Map<String, Object> stats = new HashMap<>();
        
        // Mock repository info
        Map<String, Object> repository = new HashMap<>();
        repository.put("name", repo);
        repository.put("full_name", owner + "/" + repo);
        repository.put("stargazers_count", 42);
        repository.put("forks_count", 8);
        repository.put("watchers_count", 15);
        repository.put("size", 2048);
        repository.put("default_branch", "main");
        repository.put("created_at", "2024-01-15T10:30:00Z");
        repository.put("updated_at", "2025-01-15T14:20:00Z");
        
        stats.put("repository", repository);
        
        // Add other stats by calling the individual endpoints
        stats.put("languages", getLanguageStats(owner, repo).getBody());
        stats.put("activity", getCommitActivity(owner, repo, 30).getBody());
        stats.put("fileTypes", getFileTypeDistribution(owner, repo).getBody());
        stats.put("contributors", getContributorStats(owner, repo).getBody());
        stats.put("size", getRepositorySize(owner, repo).getBody());
        stats.put("pushFrequency", getPushFrequency(owner, repo, 30).getBody());
        
        return ResponseEntity.ok(stats);
    }
}
