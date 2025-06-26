package com.example.esprit.controller;

import com.example.esprit.model.PushedFile;
import com.example.esprit.repository.PushedFilesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/github")
@RequiredArgsConstructor
public class GitHubController {

    private final PushedFilesRepository pushedFileRepository;

    /**
     * Get all repos for a user
     */
    @GetMapping("/users/{user}/repos")
    public List<String> getReposByUser(@PathVariable String user) {
        return pushedFileRepository.findDistinctRepoNamesByPusherName(user);
    }

    /**
     * Get all branches for a repo and user
     */
    @GetMapping("/users/{user}/repos/{repo}/branches")
    public List<String> getBranchesByUserAndRepo(@PathVariable String user, @PathVariable String repo) {
        return pushedFileRepository.findDistinctBranchesByRepoNameAndPusherName(repo, user);
    }

    /**
     * Get all files/folders for a repo, branch, and user
     */
    @GetMapping("/users/{user}/repos/{repo}/branches/{branch}/files")
    public List<PushedFile> getFilesByUserRepoBranch(
            @PathVariable String user,
            @PathVariable String repo,
            @PathVariable String branch
    ) {
        return pushedFileRepository.findByRepoNameAndBranchAndPusherName(repo, branch, user);
    }

    /**
     * Get file content for a specific file
     */
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
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Get distinct file extensions for a given pusher
     */
    @GetMapping("/extensions/{pusherName}")
    public List<String> getFileExtensions(@PathVariable String pusherName) {
        return pushedFileRepository.findFileExtensionsByPusherName(pusherName);
    }

    /**
     * Create or update a pushed file
     */
    @PostMapping("/users/{user}/repos/{repo}/branches/{branch}/files")
    public ResponseEntity<PushedFile> pushFile(
            @PathVariable String user,
            @PathVariable String repo,
            @PathVariable String branch,
            @RequestBody PushedFile pushedFile
    ) {
        // Set the path parameters
        pushedFile.setPusherName(user);
        pushedFile.setRepoName(repo);
        pushedFile.setBranch(branch);
        
        PushedFile savedFile = pushedFileRepository.save(pushedFile);
        return ResponseEntity.ok(savedFile);
    }
}
