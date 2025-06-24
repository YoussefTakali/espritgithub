package esprit.git_push_test.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Controller to fetch metadata directly from GitHub API
 */
@RestController
@RequestMapping("/api/github/metadata")
public class GitHubMetadataController {

    @Value("${github.token}")
    private String githubToken;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * Get commit history for a specific file
     * 
     * @param owner Repository owner
     * @param repo Repository name
     * @param path File path within the repository
     * @return List of commits that modified the file
     */
    @GetMapping("/{owner}/{repo}/commits")
    public ResponseEntity<?> getFileCommits(
            @PathVariable String owner,
            @PathVariable String repo,
            @RequestParam String path) {
        
        try {
            String url = String.format("https://api.github.com/repos/%s/%s/commits?path=%s", 
                    owner, repo, path);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(githubToken);
            headers.set("Accept", "application/vnd.github.v3+json");
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            
            ResponseEntity<List> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, List.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                List<Map<String, Object>> commits = response.getBody();
                List<Map<String, Object>> result = new ArrayList<>();
                
                for (Map<String, Object> commit : commits) {
                    Map<String, Object> metadata = new HashMap<>();
                    
                    // Extract commit info
                    if (commit.containsKey("sha")) {
                        metadata.put("commitId", commit.get("sha"));
                    }
                    
                    if (commit.containsKey("commit")) {
                        Map<String, Object> commitDetails = (Map<String, Object>) commit.get("commit");
                        
                        // Extract commit message
                        if (commitDetails.containsKey("message")) {
                            metadata.put("commitMessage", commitDetails.get("message"));
                        }
                        
                        // Extract author info
                        if (commitDetails.containsKey("author")) {
                            Map<String, Object> author = (Map<String, Object>) commitDetails.get("author");
                            metadata.put("authorName", author.get("name"));
                            metadata.put("authorEmail", author.get("email"));
                            metadata.put("authorDate", author.get("date"));
                        }
                        
                        // Extract committer info
                        if (commitDetails.containsKey("committer")) {
                            Map<String, Object> committer = (Map<String, Object>) commitDetails.get("committer");
                            metadata.put("committerName", committer.get("name"));
                            metadata.put("committerEmail", committer.get("email"));
                            metadata.put("commitDate", committer.get("date"));
                        }
                    }
                    
                    // Extract author GitHub info
                    if (commit.containsKey("author") && commit.get("author") != null) {
                        Map<String, Object> author = (Map<String, Object>) commit.get("author");
                        metadata.put("authorLogin", author.get("login"));
                        metadata.put("authorAvatar", author.get("avatar_url"));
                    }
                    
                    // Extract committer GitHub info
                    if (commit.containsKey("committer") && commit.get("committer") != null) {
                        Map<String, Object> committer = (Map<String, Object>) commit.get("committer");
                        metadata.put("committerLogin", committer.get("login"));
                        metadata.put("committerAvatar", committer.get("avatar_url"));
                    }
                    
                    result.add(metadata);
                }
                
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.status(response.getStatusCode())
                        .body("Error fetching commits: " + response.getStatusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("Error fetching commits: " + e.getMessage());
        }
    }
    
    /**
     * Get content and metadata for a specific file
     * 
     * @param owner Repository owner
     * @param repo Repository name
     * @param path File path within the repository
     * @param ref Branch or commit SHA (optional, defaults to the default branch)
     * @return File content and metadata
     */
    @GetMapping("/{owner}/{repo}/contents")
    public ResponseEntity<?> getFileContent(
            @PathVariable String owner,
            @PathVariable String repo,
            @RequestParam String path,
            @RequestParam(required = false) String ref) {
        
        try {
            String url = String.format("https://api.github.com/repos/%s/%s/contents/%s", 
                    owner, repo, path);
            
            if (ref != null && !ref.isEmpty()) {
                url += "?ref=" + ref;
            }
            
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(githubToken);
            headers.set("Accept", "application/vnd.github.v3+json");
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> content = response.getBody();
                Map<String, Object> result = new HashMap<>();
                
                // Extract basic file info
                result.put("name", content.get("name"));
                result.put("path", content.get("path"));
                result.put("sha", content.get("sha"));
                result.put("size", content.get("size"));
                result.put("type", content.get("type"));
                result.put("url", content.get("url"));
                result.put("html_url", content.get("html_url"));
                result.put("git_url", content.get("git_url"));
                result.put("download_url", content.get("download_url"));
                
                // Extract content if it's a file
                if (content.containsKey("content") && content.get("content") != null) {
                    String encodedContent = (String) content.get("content");
                    String decodedContent = new String(Base64.getDecoder().decode(encodedContent.replaceAll("\\s", "")));
                    result.put("content", decodedContent);
                }
                
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.status(response.getStatusCode())
                        .body("Error fetching file content: " + response.getStatusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("Error fetching file content: " + e.getMessage());
        }
    }
    
    /**
     * Get combined file information (content + latest commit)
     * 
     * @param owner Repository owner
     * @param repo Repository name
     * @param path File path within the repository
     * @param ref Branch or commit SHA (optional, defaults to the default branch)
     * @return Combined file information
     */
    @GetMapping("/{owner}/{repo}/file-info")
    public ResponseEntity<?> getFileInfo(
            @PathVariable String owner,
            @PathVariable String repo,
            @RequestParam String path,
            @RequestParam(required = false) String ref) {
        
        try {
            // Get file content
            ResponseEntity<?> contentResponse = getFileContent(owner, repo, path, ref);
            if (!contentResponse.getStatusCode().is2xxSuccessful()) {
                return contentResponse;
            }
            
            Map<String, Object> fileInfo = (Map<String, Object>) contentResponse.getBody();
            
            // Get commit history
            ResponseEntity<?> commitsResponse = getFileCommits(owner, repo, path);
            if (!commitsResponse.getStatusCode().is2xxSuccessful()) {
                return commitsResponse;
            }
            
            List<Map<String, Object>> commits = (List<Map<String, Object>>) commitsResponse.getBody();
            
            // Add latest commit info to file info
            if (!commits.isEmpty()) {
                Map<String, Object> latestCommit = commits.get(0);
                fileInfo.put("latestCommit", latestCommit);
            }
            
            return ResponseEntity.ok(fileInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("Error fetching file information: " + e.getMessage());
        }
    }
}
