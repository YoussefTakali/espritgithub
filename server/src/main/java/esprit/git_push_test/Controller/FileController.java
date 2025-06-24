package esprit.git_push_test.Controller;

import esprit.git_push_test.Entity.PushedFile;
import esprit.git_push_test.Repository.PushedFilesRepository;
import esprit.git_push_test.Service.RepositoryInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/files")
public class FileController {

    @Value("${github.token}")
    private String githubToken;

    @Autowired
    private PushedFilesRepository pushedFilesRepository;

    @Autowired
    private RepositoryInfoService repositoryInfoService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFiles(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam("commitMessage") String commitMessage) {

        try {
            String owner = repositoryInfoService.getRepoOwner();
            String repo = repositoryInfoService.getRepoName();
            String branch = repositoryInfoService.getBranch();

            if (owner == null || repo == null || branch == null) {
                return ResponseEntity.badRequest().body("Repository information not available. Please set up repository details first.");
            }

            for (MultipartFile file : files) {
                String fileName = file.getOriginalFilename();
                if (fileName == null || fileName.isEmpty()) {
                    continue;
                }

                // Check if file already exists in database
                PushedFile existingFile = pushedFilesRepository.findByRepoNameAndBranchAndPusherNameAndFilePath(
                        repo, branch, owner, fileName);

                boolean isUpdate = existingFile != null;
                String action = isUpdate ? "updated" : "created";

                // Push to GitHub
                boolean githubSuccess = pushFileToGitHub(owner, repo, branch, fileName, file, commitMessage, isUpdate);

                if (githubSuccess) {
                    // Save or update in database
                    saveOrUpdateFileInDatabase(file, fileName, commitMessage, owner, repo, branch, existingFile);
                    System.out.println("File " + fileName + " " + action + " successfully in GitHub and database");
                } else {
                    System.err.println("Failed to push file " + fileName + " to GitHub");
                }
            }

            return ResponseEntity.ok("Files uploaded and committed successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while uploading files: " + e.getMessage());
        }
    }

    @PostMapping("/edit")
    public ResponseEntity<String> editFile(
            @RequestParam("filePath") String filePath,
            @RequestParam("content") String content,
            @RequestParam("commitMessage") String commitMessage) {

        try {
            String owner = repositoryInfoService.getRepoOwner();
            String repo = repositoryInfoService.getRepoName();
            String branch = repositoryInfoService.getBranch();

            if (owner == null || repo == null || branch == null) {
                return ResponseEntity.badRequest().body("Repository information not available. Please set up repository details first.");
            }

            // Check if file exists in database
            PushedFile existingFile = pushedFilesRepository.findByRepoNameAndBranchAndPusherNameAndFilePath(
                    repo, branch, owner, filePath);

            if (existingFile == null) {
                return ResponseEntity.badRequest().body("File not found in database: " + filePath);
            }

            // Push edited content to GitHub
            boolean githubSuccess = pushTextContentToGitHub(owner, repo, branch, filePath, content, commitMessage);

            if (githubSuccess) {
                // Update in database
                updateFileContentInDatabase(existingFile, content, commitMessage);
                System.out.println("File " + filePath + " edited successfully in GitHub and database");
                return ResponseEntity.ok("File edited and committed successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to push edited file to GitHub");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while editing file: " + e.getMessage());
        }
    }

    @PostMapping("/configure-repository")
    public ResponseEntity<String> configureRepository(
            @RequestParam("owner") String owner,
            @RequestParam("repo") String repo,
            @RequestParam(value = "branch", defaultValue = "main") String branch) {

        try {
            repositoryInfoService.setRepoOwner(owner);
            repositoryInfoService.setRepoName(repo);
            repositoryInfoService.setBranch(branch);

            return ResponseEntity.ok("Repository configuration updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating repository configuration: " + e.getMessage());
        }
    }

    /**
     * Push a file to GitHub (create or update)
     */
    private boolean pushFileToGitHub(String owner, String repo, String branch, String fileName,
                                     MultipartFile file, String commitMessage, boolean isUpdate) {
        try {
            String content = Base64.getEncoder().encodeToString(file.getBytes());
            String url = "https://api.github.com/repos/" + owner + "/" + repo + "/contents/" + fileName;

            RestTemplate restTemplate = new RestTemplate();
            String sha = null;

            // If updating, get the current file's SHA
            if (isUpdate) {
                try {
                    HttpHeaders getHeaders = new HttpHeaders();
                    getHeaders.setBearerAuth(githubToken);
                    getHeaders.set("Accept", "application/vnd.github+json");
                    HttpEntity<Void> getEntity = new HttpEntity<>(getHeaders);

                    ResponseEntity<Map> response = restTemplate.exchange(
                            url + "?ref=" + branch, HttpMethod.GET, getEntity, Map.class);

                    if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                        sha = (String) response.getBody().get("sha");
                    }
                } catch (Exception e) {
                    System.err.println("Error getting file SHA for update: " + e.getMessage());
                }
            }

            Map<String, Object> body = new HashMap<>();
            body.put("message", commitMessage + (isUpdate ? " (updated)" : " (created)"));
            body.put("content", content);
            body.put("branch", branch);
            if (sha != null) {
                body.put("sha", sha);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(githubToken);
            headers.set("Accept", "application/vnd.github+json");
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
            return response.getStatusCode().is2xxSuccessful();

        } catch (Exception e) {
            System.err.println("Error pushing file to GitHub: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Save or update file information in the database
     */
    private void saveOrUpdateFileInDatabase(MultipartFile file, String fileName, String commitMessage,
                                            String owner, String repo, String branch, PushedFile existingFile) {
        try {
            PushedFile pushedFile = existingFile != null ? existingFile : new PushedFile();

            // Extract folder path
            String folderPath = "";
            int lastSlashIndex = fileName.lastIndexOf('/');
            if (lastSlashIndex > 0) {
                folderPath = fileName.substring(0, lastSlashIndex);
            }

            // Extract file extension
            String fileExtension = "";
            int lastDotIndex = fileName.lastIndexOf('.');
            if (lastDotIndex > 0) {
                fileExtension = fileName.substring(lastDotIndex + 1).toLowerCase();
            }

            // Set file properties
            pushedFile.setFilePath(fileName);
            pushedFile.setFolderPath(folderPath);
            pushedFile.setFileType(file.getContentType());
            pushedFile.setFileExtension(fileExtension);
            pushedFile.setCommitMessage(commitMessage);
            pushedFile.setPushedAt(LocalDateTime.now());
            pushedFile.setBranch(branch);
            pushedFile.setRepoName(repo);
            pushedFile.setPusherName(owner);

            // Handle file content based on type
            if (isBinaryFile(fileExtension)) {
                // For binary files, store metadata but not content
                pushedFile.setContent("[Binary file - " + fileExtension.toUpperCase() + " - " + formatFileSize(file.getSize()) + "]");
            } else {
                try {
                    String content = new String(file.getBytes());
                    // Clean content to avoid UTF-8 issues
                    content = cleanContent(content);
                    pushedFile.setContent(content);
                } catch (Exception e) {
                    pushedFile.setContent("[File content could not be processed]");
                }
            }

            pushedFilesRepository.save(pushedFile);

        } catch (Exception e) {
            System.err.println("Error saving file to database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Check if a file is binary based on its extension
     */
    private boolean isBinaryFile(String extension) {
        if (extension == null || extension.isEmpty()) {
            return false;
        }

        String[] binaryExtensions = {
                // Images
                "jpg", "jpeg", "png", "gif", "bmp", "tiff", "ico", "webp", "svg", "raw", "psd",
                // Audio
                "mp3", "wav", "ogg", "flac", "aac", "m4a", "wma", "opus",
                // Video
                "mp4", "avi", "mov", "wmv", "flv", "mkv", "webm", "m4v", "3gp", "mpg", "mpeg",
                // Documents
                "pdf", "doc", "docx", "ppt", "pptx", "xls", "xlsx", "odt", "ods", "odp", "rtf",
                // Archives
                "zip", "rar", "7z", "tar", "gz", "bz2", "xz", "lzma", "cab", "iso",
                // Executables
                "exe", "dll", "so", "dylib", "bin", "app", "deb", "rpm", "msi", "dmg",
                // Development
                "class", "jar", "war", "pyc", "pyo", "o", "a", "lib", "obj",
                // Database
                "db", "sqlite", "sqlite3", "mdb", "accdb", "dbf",
                // Fonts
                "ttf", "otf", "woff", "woff2", "eot",
                // Other
                "dat", "tmp", "cache", "log"
        };

        for (String binaryExt : binaryExtensions) {
            if (extension.equalsIgnoreCase(binaryExt)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Clean content to avoid UTF-8 encoding issues
     */
    private String cleanContent(String content) {
        if (content == null) {
            return "";
        }

        // Remove null bytes and other problematic characters
        return content.replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F]", "")
                .replaceAll("\\p{Cntrl}", "");
    }

    /**
     * Push text content to GitHub
     */
    private boolean pushTextContentToGitHub(String owner, String repo, String branch, String filePath,
                                            String content, String commitMessage) {
        try {
            String encodedContent = Base64.getEncoder().encodeToString(content.getBytes());
            String url = "https://api.github.com/repos/" + owner + "/" + repo + "/contents/" + filePath;

            RestTemplate restTemplate = new RestTemplate();
            String sha = null;

            // Get the current file's SHA for update
            try {
                HttpHeaders getHeaders = new HttpHeaders();
                getHeaders.setBearerAuth(githubToken);
                getHeaders.set("Accept", "application/vnd.github+json");
                HttpEntity<Void> getEntity = new HttpEntity<>(getHeaders);

                ResponseEntity<Map> response = restTemplate.exchange(
                        url + "?ref=" + branch, HttpMethod.GET, getEntity, Map.class);

                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    sha = (String) response.getBody().get("sha");
                }
            } catch (Exception e) {
                System.err.println("Error getting file SHA for update: " + e.getMessage());
                return false;
            }

            Map<String, Object> body = new HashMap<>();
            body.put("message", commitMessage + " (edited)");
            body.put("content", encodedContent);
            body.put("branch", branch);
            body.put("sha", sha);

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(githubToken);
            headers.set("Accept", "application/vnd.github+json");
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
            return response.getStatusCode().is2xxSuccessful();

        } catch (Exception e) {
            System.err.println("Error pushing text content to GitHub: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update file content in database
     */
    private void updateFileContentInDatabase(PushedFile existingFile, String content, String commitMessage) {
        try {
            existingFile.setContent(cleanContent(content));
            existingFile.setCommitMessage(commitMessage);
            existingFile.setPushedAt(LocalDateTime.now());
            pushedFilesRepository.save(existingFile);
        } catch (Exception e) {
            System.err.println("Error updating file in database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Format file size in human readable format
     */
    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }

    @PostMapping("/replace")
    public ResponseEntity<String> replaceFile(
            @RequestParam("file") MultipartFile newFile,
            @RequestParam("filePath") String filePath,
            @RequestParam("commitMessage") String commitMessage) {

        try {
            String owner = repositoryInfoService.getRepoOwner();
            String repo = repositoryInfoService.getRepoName();
            String branch = repositoryInfoService.getBranch();

            if (owner == null || repo == null || branch == null) {
                return ResponseEntity.badRequest()
                        .body("Repository information not available. Please set up repository details first.");
            }

            // Debug logging to verify inputs
            System.out.println("[DEBUG] Replace file request received:");
            System.out.println("  owner: " + owner);
            System.out.println("  repo: " + repo);
            System.out.println("  branch: " + branch);
            System.out.println("  filePath: " + filePath);
            System.out.println("  commitMessage: " + commitMessage);
            System.out.println("  newFile original filename: " + newFile.getOriginalFilename());

            // Check if the file exists in the database
            PushedFile existingFile = pushedFilesRepository.findByRepoNameAndBranchAndPusherNameAndFilePath(
                    repo, branch, owner, filePath);

            if (existingFile == null) {
                System.err.println("[ERROR] File not found in DB: " + filePath);
                return ResponseEntity.badRequest().body("File not found: " + filePath);
            }

            // Replace file content in GitHub
            boolean githubSuccess = pushFileToGitHub(owner, repo, branch, filePath, newFile, commitMessage, true);

            if (githubSuccess) {
                // Update in database
                saveOrUpdateFileInDatabase(newFile, filePath, commitMessage, owner, repo, branch, existingFile);
                return ResponseEntity.ok("File replaced and committed successfully.");
            } else {
                System.err.println("[ERROR] Failed to replace file on GitHub: " + filePath);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to replace file on GitHub.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while replacing file: " + e.getMessage());
        }
    }
}