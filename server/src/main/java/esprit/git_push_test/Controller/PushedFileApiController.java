package esprit.git_push_test.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import esprit.git_push_test.Entity.PushedFile;
import esprit.git_push_test.Repository.PushedFilesRepository;
import lombok.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PushedFileApiController {

    private final PushedFilesRepository pushedFileRepository;

    // Injecte PushedFilesRepository via le constructeur
    public PushedFileApiController(PushedFilesRepository pushedFileRepository) {
        this.pushedFileRepository = pushedFileRepository;
    }

    // 1. Get all repos for a user
    @GetMapping("/users/{user}/repos")
    public List<String> getReposByUser(@PathVariable String user) {
        return pushedFileRepository.findDistinctRepoNamesByPusherName(user);
    }

    // 2. Get all branches for a repo and user
    @GetMapping("/users/{user}/repos/{repo}/branches")
    public List<String> getBranchesByUserAndRepo(@PathVariable String user, @PathVariable String repo) {
        return pushedFileRepository.findDistinctBranchesByRepoNameAndPusherName(repo, user);
    }

    // 3. Get all files/folders for a repo, branch, and user
    @GetMapping("/users/{user}/repos/{repo}/branches/{branch}/files")
    public List<PushedFile> getFilesByUserRepoBranch(
            @PathVariable String user,
            @PathVariable String repo,
            @PathVariable String branch
    ) {
        return pushedFileRepository.findByRepoNameAndBranchAndPusherName(repo, branch, user);
    }

    // 4. Get file content for a specific file
    @GetMapping("/users/{user}/repos/{repo}/branches/{branch}/files/content")
    public ResponseEntity<String> getFileContent(
            @RequestParam String filePath,
            @PathVariable String user,
            @PathVariable String repo,
            @PathVariable String branch
    ) {
        PushedFile file = pushedFileRepository.findByRepoNameAndBranchAndPusherNameAndFilePath(repo, branch, user, filePath);
        if (file != null) {
            return ResponseEntity.ok(file.getContent());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 5. Get distinct file extensions for a given pusher
    @GetMapping("/extensions/{pusherName}")
    public List<String> getFileExtensions(@PathVariable String pusherName) {
        return pushedFileRepository.findFileExtensionsByPusherName(pusherName);
    }
    @PostMapping("/users/{user}/repos/{repo}/branches/{branch}/files")
    public ResponseEntity<PushedFile> pushFile(
            @PathVariable String user,
            @PathVariable String repo,
            @PathVariable String branch,
            @RequestBody PushedFile pushedFile
    ) {
        pushedFile.setPusherName(user);
        pushedFile.setRepoName(repo);
        pushedFile.setBranch(branch);
        PushedFile savedFile = pushedFileRepository.save(pushedFile);
        return ResponseEntity.ok(savedFile);
    }

}
