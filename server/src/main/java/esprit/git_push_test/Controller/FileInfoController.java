package esprit.git_push_test.Controller;

import esprit.git_push_test.Entity.PushedFile;
import esprit.git_push_test.Repository.PushedFilesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller to retrieve file information including pusher, commit message, and timestamp
 */
@RestController
@RequestMapping("/api/files")
public class FileInfoController {

    @Autowired
    private PushedFilesRepository pushedFileRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${github.token}")
    private String githubToken;

    @Autowired
    private HttpServletRequest request;

    /**
     * Get all files with their information
     *
     * @return List of files with their information
     */
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllFiles() {
        List<PushedFile> files = pushedFileRepository.findAll();
        List<Map<String, Object>> result = files.stream()
                .map(this::convertToFileInfo)
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    /**
     * Get all files for a specific user
     *
     * @param user Username
     * @return List of files with their information
     */
    @GetMapping("/users/{user}")
    public ResponseEntity<List<Map<String, Object>>> getUserFiles(
            @PathVariable String user) {

        // Get all repos for this user
        List<String> repos = pushedFileRepository.findDistinctRepoNamesByPusherName(user);

        List<PushedFile> files = new ArrayList<>();
        for (String repo : repos) {
            // Get all branches for this repo
            List<String> branches = pushedFileRepository.findDistinctBranchesByRepoNameAndPusherName(repo, user);

            for (String branch : branches) {
                files.addAll(pushedFileRepository.findByRepoNameAndBranchAndPusherName(repo, branch, user));
            }
        }

        List<Map<String, Object>> result = files.stream()
                .map(this::convertToFileInfo)
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    /**
     * Get files for a specific repository
     *
     * @param owner Repository owner
     * @param repo Repository name
     * @return List of files with their information
     */
    @GetMapping("/{owner}/{repo}")
    public ResponseEntity<List<Map<String, Object>>> getRepoFiles(
            @PathVariable String owner,
            @PathVariable String repo) {

        // Get all branches for this repo
        List<String> branches = pushedFileRepository.findDistinctBranchesByRepoNameAndPusherName(repo, owner);

        List<PushedFile> files = new ArrayList<>();
        for (String branch : branches) {
            files.addAll(pushedFileRepository.findByRepoNameAndBranchAndPusherName(repo, branch, owner));
        }

        List<Map<String, Object>> result = files.stream()
                .map(this::convertToFileInfo)
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    /**
     * Get files for a specific repository and branch
     *
     * @param owner Repository owner
     * @param repo Repository name
     * @param branch Branch name
     * @return List of files with their information
     */
    @GetMapping("/{owner}/{repo}/{branch}")
    public ResponseEntity<List<Map<String, Object>>> getRepoBranchFiles(
            @PathVariable String owner,
            @PathVariable String repo,
            @PathVariable String branch) {

        List<PushedFile> files = pushedFileRepository.findByRepoNameAndBranchAndPusherName(repo, branch, owner);
        List<Map<String, Object>> result = files.stream()
                .map(this::convertToFileInfo)
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    /**
     * Get information for a specific file
     *
     * @param owner Repository owner
     * @param repo Repository name
     * @param branch Branch name
     * @param filePath File path
     * @return File information
     */
    @GetMapping("/{owner}/{repo}/{branch}/**")
    public ResponseEntity<Map<String, Object>> getFileInfo(
            @PathVariable String owner,
            @PathVariable String repo,
            @PathVariable String branch,
            @RequestParam(required = false) String filePath) {

        if (filePath == null) {
            // Extract file path from the URL
            String requestUrl = request.getRequestURL().toString();
            String basePath = String.format("/api/files/%s/%s/%s/", owner, repo, branch);
            int basePathIndex = requestUrl.indexOf(basePath) + basePath.length();
            filePath = requestUrl.substring(basePathIndex);
        }

        PushedFile file = pushedFileRepository.findByRepoNameAndBranchAndPusherNameAndFilePath(repo, branch, owner, filePath);

        if (file != null) {
            Map<String, Object> result = convertToFileInfo(file);

            // Fetch additional commit information from GitHub
            try {
                Map<String, Object> commitInfo = getCommitInfo(owner, repo, file.getCommitId());
                if (commitInfo != null) {
                    result.putAll(commitInfo);
                }
            } catch (Exception e) {
                System.err.println("Error fetching commit info: " + e.getMessage());
            }

            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Convert PushedFile to a map with file information
     */
    private Map<String, Object> convertToFileInfo(PushedFile file) {
        Map<String, Object> info = new HashMap<>();
        info.put("id", file.getId());
        info.put("repoName", file.getRepoName());
        info.put("filePath", file.getFilePath());
        info.put("fileType", file.getFileType());
        info.put("fileExtension", file.getFileExtension());
        info.put("branch", file.getBranch());
        info.put("commitId", file.getCommitId());
        info.put("commitMessage", file.getCommitMessage());
        info.put("pusherName", file.getPusherName());

        // Add timestamp if available
        if (file.getPushedAt() != null) {
            info.put("timestamp", file.getPushedAt().toString());
        }

        return info;
    }

    /**
     * Get additional commit information from GitHub
     */
    private Map<String, Object> getCommitInfo(String owner, String repo, String commitId) {
        String url = String.format("https://api.github.com/repos/%s/%s/commits/%s", owner, repo, commitId);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(githubToken);
        headers.set("Accept", "application/vnd.github.v3+json");
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> commitInfo = response.getBody();
                Map<String, Object> result = new HashMap<>();

                // Extract commit timestamp
                if (commitInfo.containsKey("commit")) {
                    Map<String, Object> commit = (Map<String, Object>) commitInfo.get("commit");
                    if (commit.containsKey("committer")) {
                        Map<String, Object> committer = (Map<String, Object>) commit.get("committer");
                        if (committer.containsKey("date")) {
                            result.put("commitDate", committer.get("date"));
                        }
                    }

                    // Extract commit message if not already available
                    if (commit.containsKey("message")) {
                        result.put("fullCommitMessage", commit.get("message"));
                    }
                }

                // Extract committer information
                if (commitInfo.containsKey("committer")) {
                    Map<String, Object> committer = (Map<String, Object>) commitInfo.get("committer");
                    if (committer.containsKey("login")) {
                        result.put("committerLogin", committer.get("login"));
                    }
                    if (committer.containsKey("avatar_url")) {
                        result.put("committerAvatar", committer.get("avatar_url"));
                    }
                }

                return result;
            }
        } catch (Exception e) {
            System.err.println("Error fetching commit info from GitHub: " + e.getMessage());
        }

        return null;
    }
}
