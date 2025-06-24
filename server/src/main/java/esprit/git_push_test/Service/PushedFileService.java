package esprit.git_push_test.Service;

import esprit.git_push_test.Entity.PushedFile;
import esprit.git_push_test.Repository.PushedFilesRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.nio.file.Paths;

@Service
public class PushedFileService {
    private final PushedFilesRepository pushedFileRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    public PushedFileService(PushedFilesRepository repo) {
        this.pushedFileRepository = repo;
    }

    public void saveFilesFromPushEvent(String repoOwner, String repoName, String commitId, String commitMessage, List<String> filePaths, String githubToken, String branch, String pusherName) {
        for (String filePath : filePaths) {
            try {
                // Build API URL
                String url = String.format("https://api.github.com/repos/%s/%s/contents/%s?ref=%s", repoOwner, repoName, filePath, commitId);

                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(githubToken);
                headers.set("Accept", "application/vnd.github.v3+json");
                HttpEntity<Void> entity = new HttpEntity<>(headers);

                ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
                if (response.getStatusCode().is2xxSuccessful()) {
                    Map body = response.getBody();
                    String fileType = (String) body.get("type");

                    if ("dir".equals(fileType)) {
                        // If it's a directory, get all files in it
                        List<Map<String, Object>> contents = (List<Map<String, Object>>) body.get("content");
                        for (Map<String, Object> item : contents) {
                            String itemPath = (String) item.get("path");
                            String itemType = (String) item.get("type");

                            if ("file".equals(itemType)) {
                                // Process individual file
                                processFile(repoOwner, repoName, commitId, commitMessage, itemPath, githubToken, branch, pusherName);
                            } else if ("dir".equals(itemType)) {
                                // Recursively process subdirectory
                                saveFilesFromPushEvent(repoOwner, repoName, commitId, commitMessage, List.of(itemPath), githubToken, branch, pusherName);
                            }
                        }
                    } else {
                        // Process individual file
                        processFile(repoOwner, repoName, commitId, commitMessage, filePath, githubToken, branch, pusherName);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error processing file " + filePath + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void processFile(String repoOwner, String repoName, String commitId, String commitMessage, String filePath, String githubToken, String branch, String pusherName) {
        try {
            String url = String.format("https://api.github.com/repos/%s/%s/contents/%s?ref=%s", repoOwner, repoName, filePath, commitId);

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(githubToken);
            headers.set("Accept", "application/vnd.github.v3+json");
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                Map body = response.getBody();
                String encodedContent = (String) body.get("content");
                String fileType = (String) body.get("type");

                // Check if this is a binary file based on extension
                String fileExtension = "";
                int lastDotIndex = filePath.lastIndexOf('.');
                if (lastDotIndex > 0) {
                    fileExtension = filePath.substring(lastDotIndex + 1).toLowerCase();
                }

                // List of common binary file extensions
                boolean isBinaryFile = isBinaryFileExtension(fileExtension);

                String content;
                if (isBinaryFile) {
                    // For binary files, just store a placeholder
                    content = "[Binary file - content not stored]";
                } else {
                    // For text files, decode and clean the content
                    try {
                        content = new String(Base64.getDecoder().decode(encodedContent.replaceAll("\\s", "")));
                        content = cleanContent(content);
                    } catch (Exception e) {
                        // If decoding fails, it might be binary data
                        System.err.println("Error decoding content for " + filePath + ": " + e.getMessage());
                        content = "[File content could not be decoded]";
                    }
                }

                // Extract folder path
                String folderPath = "";
                int lastSlashIndex = filePath.lastIndexOf('/');
                if (lastSlashIndex > 0) {
                    folderPath = filePath.substring(0, lastSlashIndex);
                }

                PushedFile pushedFile = new PushedFile();
                pushedFile.setRepoName(repoName);
                pushedFile.setCommitId(commitId);
                pushedFile.setCommitMessage(commitMessage);
                pushedFile.setFilePath(filePath);
                pushedFile.setFolderPath(folderPath);
                pushedFile.setFileType(fileType);
                pushedFile.setFileExtension(fileExtension);
                pushedFile.setContent(content);
                pushedFile.setBranch(branch);
                pushedFile.setPusherName(pusherName);
                pushedFileRepository.save(pushedFile);
            }
        } catch (Exception e) {
            System.err.println("Error processing file " + filePath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String cleanContent(String content) {
        if (content == null) {
            return "";
        }
        // Remove null bytes and other invalid UTF-8 characters
        return content.replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F]", "")
                     .replaceAll("[^\\x20-\\x7E\\x0A\\x0D]", "");
    }

    /**
     * Check if a file is likely binary based on its extension
     */
    private boolean isBinaryFileExtension(String extension) {
        if (extension == null || extension.isEmpty()) {
            return false;
        }

        // Common binary file extensions
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
}