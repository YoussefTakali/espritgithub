package esprit.git_push_test.Service;

import esprit.git_push_test.Entity.PushedFile;
import esprit.git_push_test.Repository.PushedFilesRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class PushedFileService {
    private final PushedFilesRepository pushedFileRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    public PushedFileService(PushedFilesRepository repo) {
        this.pushedFileRepository = repo;
    }

    public void saveFilesFromPushEvent(String repoOwner, String repoName, String commitId, List<String> filePaths, String githubToken) {
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
                    String encodedContent = (String) body.get("content");
                    String fileType = (String) body.get("type");
                    
                    // Decode and clean the content
                    String content = new String(Base64.getDecoder().decode(encodedContent.replaceAll("\\s", "")));
                    content = cleanContent(content);

                    PushedFile pushedFile = new PushedFile();
                    pushedFile.setRepoName(repoName);
                    pushedFile.setCommitId(commitId);
                    pushedFile.setFilePath(filePath);
                    pushedFile.setFileType(fileType);
                    pushedFile.setContent(content);
                    pushedFileRepository.save(pushedFile);
                }
            } catch (Exception e) {
                System.err.println("Error processing file " + filePath + ": " + e.getMessage());
                e.printStackTrace();
            }
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
}