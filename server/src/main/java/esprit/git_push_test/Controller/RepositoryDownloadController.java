package esprit.git_push_test.Controller;

import esprit.git_push_test.Service.RepositoryDownloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller to handle repository download operations
 */
@RestController
@RequestMapping("/api/repos")
public class RepositoryDownloadController {

    @Autowired
    private RepositoryDownloadService repositoryDownloadService;

    /**
     * Get repository information including clone URLs
     *
     * @param owner The repository owner
     * @param repo The repository name
     * @return Repository information including clone URLs
     */
    @GetMapping("/{owner}/{repo}/info")
    public ResponseEntity<Map<String, Object>> getRepositoryInfo(
            @PathVariable String owner,
            @PathVariable String repo) {

        try {
            Map<String, Object> repoInfo = repositoryDownloadService.getRepositoryInfo(owner, repo);
            return ResponseEntity.ok(repoInfo);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error fetching repository information: " + e.getMessage()));
        }
    }

    /**
     * Download repository as ZIP file
     *
     * @param owner The repository owner
     * @param repo The repository name
     * @param branch The branch to download (optional, defaults to the default branch)
     * @return ZIP file as a streaming response
     */
    /**
     * Download repository as ZIP file
     * This endpoint redirects to GitHub's download URL
     */
    @GetMapping("/{owner}/{repo}/download")
    public ResponseEntity<Void> downloadRepository(
            @PathVariable String owner,
            @PathVariable String repo,
            @RequestParam(required = false, defaultValue = "main") String branch) {

        try {
            // Build the GitHub download URL directly without using the API
            String downloadUrl = String.format("https://github.com/%s/%s/archive/refs/heads/%s.zip",
                    owner, repo, branch);

            System.out.println("Redirecting to GitHub download URL: " + downloadUrl);

            // Redirect to GitHub's download URL
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create(downloadUrl));

            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Test endpoint to verify download functionality
     */
    @GetMapping("/test-download")
    public ResponseEntity<String> testDownload() {
        return ResponseEntity.ok("Download endpoint is working. Try using /{owner}/{repo}/download to download a repository.");
    }

    /**
     * Alternative download method that proxies the ZIP file through the server
     * This may help with corrupted downloads in some cases
     */
    @GetMapping("/{owner}/{repo}/proxy-download")
    public ResponseEntity<byte[]> proxyDownload(
            @PathVariable String owner,
            @PathVariable String repo,
            @RequestParam(required = false, defaultValue = "main") String branch) {

        try {
            // Build the GitHub download URL
            String downloadUrl = String.format("https://github.com/%s/%s/archive/refs/heads/%s.zip",
                    owner, repo, branch);

            System.out.println("Proxying download from: " + downloadUrl);

            // Create a URL connection
            URL url = new URL(downloadUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Read the file data
            try (InputStream inputStream = connection.getInputStream();
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                byte[] zipData = outputStream.toByteArray();

                // Set up response headers
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDisposition(ContentDisposition.attachment()
                        .filename(repo + "-" + branch + ".zip").build());
                headers.setCacheControl("no-cache, no-store, must-revalidate");
                headers.setPragma("no-cache");
                headers.setExpires(0);

                return ResponseEntity.ok()
                        .headers(headers)
                        .body(zipData);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get repository archive URL
     *
     * @param owner The repository owner
     * @param repo The repository name
     * @param branch The branch to download (optional, defaults to the default branch)
     * @return Archive URL information
     */
    @GetMapping("/{owner}/{repo}/archive-url")
    public ResponseEntity<Map<String, String>> getArchiveUrl(
            @PathVariable String owner,
            @PathVariable String repo,
            @RequestParam(required = false, defaultValue = "main") String branch) {

        try {
            // Build the GitHub download URL directly without using the API
            String downloadUrl = String.format("https://github.com/%s/%s/archive/refs/heads/%s.zip",
                    owner, repo, branch);

            Map<String, String> result = new HashMap<>();
            result.put("archive_url", downloadUrl);
            result.put("branch", branch);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error generating archive URL: " + e.getMessage()));
        }
    }

    /**
     * Direct download endpoint that redirects to GitHub
     * This is a simpler alternative that works even when API rate limits are hit
     *
     * @param owner The repository owner
     * @param repo The repository name
     * @param branch The branch to download (optional, defaults to main if not specified)
     * @return Redirect to GitHub download URL
     */
    @GetMapping("/{owner}/{repo}/direct-download")
    public ResponseEntity<Void> directDownload(
            @PathVariable String owner,
            @PathVariable String repo,
            @RequestParam(required = false, defaultValue = "main") String branch) {

        try {
            // Build the GitHub download URL directly without using the API
            String downloadUrl = String.format("https://github.com/%s/%s/archive/refs/heads/%s.zip",
                    owner, repo, branch);

            System.out.println("Redirecting to GitHub download URL: " + downloadUrl);

            // Redirect to GitHub's download URL
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create(downloadUrl));

            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
