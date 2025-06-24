package esprit.git_push_test.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Service to handle repository download operations
 */
@Service
public class RepositoryDownloadService {

    @Value("${github.token}")
    private String githubToken;

    private final RestTemplate restTemplate;

    public RepositoryDownloadService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Get repository information including clone URLs
     *
     * @param owner The repository owner
     * @param repo The repository name
     * @return Repository information including clone URLs
     */
    public Map<String, Object> getRepositoryInfo(String owner, String repo) {
        try {
            // Build GitHub API URL
            String apiUrl = String.format("https://api.github.com/repos/%s/%s", owner, repo);

            // Set up headers
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(githubToken);
            headers.set("Accept", "application/vnd.github+json");
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            // Make API request
            ResponseEntity<Map> response = restTemplate.exchange(
                    apiUrl, HttpMethod.GET, entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> repoInfo = response.getBody();

                // Extract relevant information
                Map<String, Object> result = new HashMap<>();
                result.put("name", repoInfo.get("name"));
                result.put("full_name", repoInfo.get("full_name"));
                result.put("description", repoInfo.get("description"));
                result.put("html_url", repoInfo.get("html_url"));
                result.put("clone_url", repoInfo.get("clone_url"));
                result.put("ssh_url", repoInfo.get("ssh_url"));
                result.put("default_branch", repoInfo.get("default_branch"));

                return result;
            } else {
                throw new RuntimeException("Failed to fetch repository information");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error fetching repository information: " + e.getMessage(), e);
        }
    }

    /**
     * Get the default branch for a repository
     *
     * @param owner The repository owner
     * @param repo The repository name
     * @return The default branch name
     */
    public String getDefaultBranch(String owner, String repo) {
        Map<String, Object> repoInfo = getRepositoryInfo(owner, repo);
        return (String) repoInfo.get("default_branch");
    }

    /**
     * Get the download URL for a repository ZIP archive
     *
     * @param owner The repository owner
     * @param repo The repository name
     * @param branch The branch to download (optional, defaults to the default branch)
     * @return The download URL
     */
    public String getRepositoryDownloadUrl(String owner, String repo, String branch) {
        if (branch == null || branch.isEmpty()) {
            branch = getDefaultBranch(owner, repo);
        }

        // GitHub's URL format for downloading a repository as a ZIP file
        return String.format("https://github.com/%s/%s/archive/refs/heads/%s.zip",
                owner, repo, branch);
    }

    /**
     * Get the API URL for a repository archive
     *
     * @param owner The repository owner
     * @param repo The repository name
     * @param branch The branch to download
     * @return The GitHub API URL for the archive
     */
    public String getRepositoryApiArchiveUrl(String owner, String repo, String branch) {
        return String.format("https://api.github.com/repos/%s/%s/zipball/%s",
                owner, repo, branch);
    }

    /**
     * Download repository content as a byte array
     *
     * @param owner The repository owner
     * @param repo The repository name
     * @param branch The branch to download (optional, defaults to the default branch)
     * @return The repository content as a byte array
     * @throws IOException If an I/O error occurs
     */
    public byte[] downloadRepositoryAsBytes(String owner, String repo, String branch) throws IOException {
        // Try using the GitHub API with authentication first
        try {
            String apiUrl = getRepositoryApiArchiveUrl(owner, repo, branch);

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(githubToken);
            headers.set("Accept", "application/vnd.github.v3+json");
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            // Use RestTemplate to get the response with proper authentication
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    apiUrl, HttpMethod.GET, entity, byte[].class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
        } catch (Exception e) {
            System.err.println("Error using GitHub API for download: " + e.getMessage());
            // Fall back to direct download if API fails
        }

        // Fallback: Try direct download from GitHub
        String downloadUrl = getRepositoryDownloadUrl(owner, repo, branch);

        try (InputStream inputStream = new URL(downloadUrl).openStream();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[8192]; // Larger buffer for better performance
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            return outputStream.toByteArray();
        }
    }
}
