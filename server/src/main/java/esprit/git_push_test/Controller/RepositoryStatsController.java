package esprit.git_push_test.Controller;

import esprit.git_push_test.Service.RepositoryStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
public class RepositoryStatsController {

    @Autowired
    private RepositoryStatsService repositoryStatsService;

    /**
     * Test endpoint to verify the controller is working
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testEndpoint() {
        Map<String, Object> response = Map.of(
            "status", "success",
            "message", "RepositoryStatsController is working!",
            "timestamp", System.currentTimeMillis()
        );
        return ResponseEntity.ok(response);
    }

    /**
     * Check GitHub API rate limit status
     */
    @GetMapping("/rate-limit")
    public ResponseEntity<Map<String, Object>> checkRateLimit() {
        try {
            Map<String, Object> rateLimitInfo = repositoryStatsService.checkGitHubRateLimit();
            return ResponseEntity.ok(rateLimitInfo);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error checking rate limit: " + e.getMessage()));
        }
    }

    /**
     * Get comprehensive repository statistics
     */
    @GetMapping("/{owner}/{repo}")
    public ResponseEntity<Map<String, Object>> getRepositoryStats(
            @PathVariable String owner,
            @PathVariable String repo,
            @RequestParam(required = false) String branch) {

        try {
            Map<String, Object> stats = repositoryStatsService.getRepositoryStatistics(owner, repo, branch);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error fetching repository statistics: " + e.getMessage()));
        }
    }

    /**
     * Get language statistics for repository
     */
    @GetMapping("/{owner}/{repo}/languages")
    public ResponseEntity<Map<String, Object>> getLanguageStats(
            @PathVariable String owner,
            @PathVariable String repo) {

        try {
            Map<String, Object> languageStats = repositoryStatsService.getLanguageStatistics(owner, repo);
            return ResponseEntity.ok(languageStats);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error fetching language statistics: " + e.getMessage()));
        }
    }

    /**
     * Get commit activity statistics (original format)
     */
    @GetMapping("/{owner}/{repo}/activity")
    public ResponseEntity<Map<String, Object>> getCommitActivity(
            @PathVariable String owner,
            @PathVariable String repo,
            @RequestParam(required = false, defaultValue = "30") int days) {

        try {
            Map<String, Object> activity = repositoryStatsService.getCommitActivity(owner, repo, days);
            return ResponseEntity.ok(activity);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error fetching commit activity: " + e.getMessage()));
        }
    }

    /**
     * Get commit activity statistics in frontend format (ActivityStats[])
     */
    @GetMapping("/{owner}/{repo}/activity-array")
    public ResponseEntity<List<Map<String, Object>>> getCommitActivityArray(
            @PathVariable String owner,
            @PathVariable String repo,
            @RequestParam(required = false, defaultValue = "30") int days) {

        try {
            List<Map<String, Object>> activity = repositoryStatsService.getCommitActivityArray(owner, repo, days);
            return ResponseEntity.ok(activity);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(List.of(Map.of("error", "Error fetching commit activity: " + e.getMessage())));
        }
    }

    /**
     * Get file type distribution (original format)
     */
    @GetMapping("/{owner}/{repo}/file-types")
    public ResponseEntity<Map<String, Object>> getFileTypeDistribution(
            @PathVariable String owner,
            @PathVariable String repo) {

        try {
            Map<String, Object> fileTypes = repositoryStatsService.getFileTypeDistribution(owner, repo);
            return ResponseEntity.ok(fileTypes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error fetching file type distribution: " + e.getMessage()));
        }
    }

    /**
     * Get file type distribution in frontend format (FileTypeStats[])
     */
    @GetMapping("/{owner}/{repo}/file-types-array")
    public ResponseEntity<List<Map<String, Object>>> getFileTypeDistributionArray(
            @PathVariable String owner,
            @PathVariable String repo) {

        try {
            List<Map<String, Object>> fileTypes = repositoryStatsService.getFileTypeDistributionArray(owner, repo);
            return ResponseEntity.ok(fileTypes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(List.of(Map.of("error", "Error fetching file type distribution: " + e.getMessage())));
        }
    }

    /**
     * Get contributor statistics (original format)
     */
    @GetMapping("/{owner}/{repo}/contributors")
    public ResponseEntity<Map<String, Object>> getContributorStats(
            @PathVariable String owner,
            @PathVariable String repo) {

        try {
            Map<String, Object> contributors = repositoryStatsService.getContributorStatistics(owner, repo);
            return ResponseEntity.ok(contributors);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error fetching contributor statistics: " + e.getMessage()));
        }
    }

    /**
     * Get contributor statistics in frontend format (ContributorStats[])
     */
    @GetMapping("/{owner}/{repo}/contributors-array")
    public ResponseEntity<List<Map<String, Object>>> getContributorStatsArray(
            @PathVariable String owner,
            @PathVariable String repo) {

        try {
            List<Map<String, Object>> contributors = repositoryStatsService.getContributorStatisticsArray(owner, repo);
            return ResponseEntity.ok(contributors);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(List.of(Map.of("error", "Error fetching contributor statistics: " + e.getMessage())));
        }
    }

    /**
     * Get contributors for sidebar (GitHub API + avatars)
     */
    @GetMapping("/{owner}/{repo}/sidebar/contributors")
    public ResponseEntity<List<Map<String, Object>>> getSidebarContributors(
            @PathVariable String owner,
            @PathVariable String repo) {

        try {
            List<Map<String, Object>> contributors = repositoryStatsService.getSidebarContributors(owner, repo);
            return ResponseEntity.ok(contributors);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(List.of(Map.of("error", "Error fetching contributors: " + e.getMessage())));
        }
    }

    /**
     * Get collaborators (users with access permissions)
     */
    @GetMapping("/{owner}/{repo}/collaborators")
    public ResponseEntity<List<Map<String, Object>>> getRepositoryCollaborators(
            @PathVariable String owner,
            @PathVariable String repo) {

        try {
            List<Map<String, Object>> collaborators = repositoryStatsService.getRepositoryCollaborators(owner, repo);
            return ResponseEntity.ok(collaborators);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(List.of(Map.of("error", "Error fetching collaborators: " + e.getMessage())));
        }
    }

    /**
     * Get detailed contributors with commit messages and dates for any repository
     */
    @GetMapping("/{owner}/{repo}/contributors/detailed")
    public ResponseEntity<Map<String, Object>> getDetailedContributors(
            @PathVariable String owner,
            @PathVariable String repo,
            @RequestParam(required = false) Integer limit) {

        try {
            Map<String, Object> contributors = repositoryStatsService.getDetailedContributors(owner, repo, limit);
            return ResponseEntity.ok(contributors);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error fetching detailed contributors: " + e.getMessage()));
        }
    }

    /**
     * Get repository files with real GitHub commit information
     */
    @GetMapping("/{owner}/{repo}/files")
    public ResponseEntity<List<Map<String, Object>>> getRepositoryFiles(
            @PathVariable String owner,
            @PathVariable String repo,
            @RequestParam(required = false) String path) {

        try {
            List<Map<String, Object>> files = repositoryStatsService.getRepositoryFilesWithCommitInfo(owner, repo, path);
            return ResponseEntity.ok(files);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(List.of(Map.of("error", "Error fetching repository files: " + e.getMessage())));
        }
    }



    /**
     * Get languages for sidebar (GitHub API + local analysis)
     */
    @GetMapping("/{owner}/{repo}/sidebar/languages")
    public ResponseEntity<List<Map<String, Object>>> getSidebarLanguages(
            @PathVariable String owner,
            @PathVariable String repo) {

        try {
            List<Map<String, Object>> languages = repositoryStatsService.getSidebarLanguages(owner, repo);
            return ResponseEntity.ok(languages);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(List.of(Map.of("error", "Error fetching languages: " + e.getMessage())));
        }
    }

    /**
     * Get repository metadata for sidebar (GitHub API)
     */
    @GetMapping("/{owner}/{repo}/sidebar/metadata")
    public ResponseEntity<Map<String, Object>> getSidebarMetadata(
            @PathVariable String owner,
            @PathVariable String repo) {

        try {
            Map<String, Object> metadata = repositoryStatsService.getSidebarMetadata(owner, repo);
            return ResponseEntity.ok(metadata);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error fetching repository metadata: " + e.getMessage()));
        }
    }

    /**
     * Get repository size and file count statistics
     */
    @GetMapping("/{owner}/{repo}/size")
    public ResponseEntity<Map<String, Object>> getRepositorySize(
            @PathVariable String owner,
            @PathVariable String repo) {

        try {
            Map<String, Object> sizeStats = repositoryStatsService.getRepositorySizeStats(owner, repo);
            return ResponseEntity.ok(sizeStats);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error fetching repository size: " + e.getMessage()));
        }
    }

    /**
     * Get push frequency statistics
     */
    @GetMapping("/{owner}/{repo}/push-frequency")
    public ResponseEntity<Map<String, Object>> getPushFrequency(
            @PathVariable String owner,
            @PathVariable String repo,
            @RequestParam(required = false, defaultValue = "30") int days) {

        try {
            Map<String, Object> pushStats = repositoryStatsService.getPushFrequencyStats(owner, repo, days);
            return ResponseEntity.ok(pushStats);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error fetching push frequency: " + e.getMessage()));
        }
    }
}
