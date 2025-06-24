package esprit.git_push_test.Controller;

import esprit.git_push_test.Service.RepositoryDownloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller to handle repository cloning operations
 */
@RestController
@RequestMapping("/api/clone")
public class RepositoryCloneController {

    @Autowired
    private RepositoryDownloadService repositoryDownloadService;

    /**
     * Get clone URLs for a repository
     *
     * @param owner The repository owner
     * @param repo The repository name
     * @return Clone URLs for the repository
     */
    @GetMapping("/{owner}/{repo}")
    public ResponseEntity<Map<String, Object>> getCloneUrls(
            @PathVariable String owner,
            @PathVariable String repo) {

        try {
            // Build clone URLs directly without using the API
            String httpsUrl = String.format("https://github.com/%s/%s.git", owner, repo);
            String sshUrl = String.format("git@github.com:%s/%s.git", owner, repo);

            Map<String, Object> result = new HashMap<>();
            result.put("repository", owner + "/" + repo);
            result.put("https_url", httpsUrl);
            result.put("ssh_url", sshUrl);
            result.put("default_branch", "main"); // Default to main

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error generating clone URLs: " + e.getMessage()));
        }
    }

    /**
     * Get clone command for a repository
     *
     * @param owner The repository owner
     * @param repo The repository name
     * @param protocol The protocol to use (https or ssh)
     * @return Clone command for the repository
     */
    @GetMapping("/{owner}/{repo}/command")
    public ResponseEntity<Map<String, String>> getCloneCommand(
            @PathVariable String owner,
            @PathVariable String repo,
            @RequestParam(defaultValue = "https") String protocol) {

        try {
            // Build clone URLs directly without using the API
            String httpsUrl = String.format("https://github.com/%s/%s.git", owner, repo);
            String sshUrl = String.format("git@github.com:%s/%s.git", owner, repo);

            String cloneUrl;
            if ("ssh".equalsIgnoreCase(protocol)) {
                cloneUrl = sshUrl;
            } else {
                cloneUrl = httpsUrl;
            }

            String cloneCommand = "git clone " + cloneUrl;

            Map<String, String> result = new HashMap<>();
            result.put("command", cloneCommand);
            result.put("url", cloneUrl);
            result.put("protocol", protocol);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error generating clone command: " + e.getMessage()));
        }
    }
}
