package com.example.esprit.service;

import com.example.esprit.model.PushedFile;
import com.example.esprit.repository.PushedFilesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PushedFileService {

    @Autowired
    private PushedFilesRepository pushedFilesRepository;

    public PushedFile savePushedFile(PushedFile pushedFile) {
        return pushedFilesRepository.save(pushedFile);
    }

    public List<PushedFile> getAllPushedFiles() {
        return pushedFilesRepository.findAll();
    }

    public List<PushedFile> getFilesByRepoAndBranch(String repoName, String branch) {
        return pushedFilesRepository.findByRepoNameAndBranch(repoName, branch);
    }

    public List<String> getReposByUser(String pusherName) {
        return pushedFilesRepository.findDistinctRepoNamesByPusherName(pusherName);
    }

    public List<String> getBranchesByUserAndRepo(String pusherName, String repoName) {
        return pushedFilesRepository.findDistinctBranchesByRepoNameAndPusherName(repoName, pusherName);
    }

    public List<PushedFile> getFilesByUserRepoBranch(String pusherName, String repoName, String branch) {
        return pushedFilesRepository.findByRepoNameAndBranchAndPusherName(repoName, branch, pusherName);
    }

    public PushedFile getFileContent(String pusherName, String repoName, String branch, String filePath) {
        return pushedFilesRepository.findByRepoNameAndBranchAndPusherNameAndFilePath(repoName, branch, pusherName, filePath);
    }

    public List<String> getFileExtensionsByPusher(String pusherName) {
        return pushedFilesRepository.findFileExtensionsByPusherName(pusherName);
    }

    public PushedFile createPushedFile(String repoName, String commitId, String filePath, 
                                     String fileType, String content, String branch, 
                                     String pusherName, String commitMessage) {
        PushedFile pushedFile = PushedFile.builder()
                .repoName(repoName)
                .commitId(commitId)
                .filePath(filePath)
                .fileType(fileType)
                .content(content)
                .branch(branch)
                .pusherName(pusherName)
                .commitMessage(commitMessage)
                .pushedAt(LocalDateTime.now())
                .build();

        // Extract file extension
        if (filePath != null && filePath.contains(".")) {
            String extension = filePath.substring(filePath.lastIndexOf(".") + 1);
            pushedFile.setFileExtension(extension);
        }

        // Extract folder path
        if (filePath != null && filePath.contains("/")) {
            String folderPath = filePath.substring(0, filePath.lastIndexOf("/"));
            pushedFile.setFolderPath(folderPath);
        }

        return pushedFilesRepository.save(pushedFile);
    }
}
