package com.example.esprit.service;

import com.example.esprit.model.PushedFile;
import com.example.esprit.repository.PushedFilesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PushedFileService {

    private final PushedFilesRepository pushedFilesRepository;

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

    public PushedFile createPushedFile(PushedFile.PushedFileBuilder builder) {
        PushedFile pushedFile = builder
                .pushedAt(LocalDateTime.now())
                .build();

        // Extract file extension
        if (pushedFile.getFilePath() != null && pushedFile.getFilePath().contains(".")) {
            String extension = pushedFile.getFilePath().substring(pushedFile.getFilePath().lastIndexOf(".") + 1);
            pushedFile.setFileExtension(extension);
        }

        // Extract folder path
        if (pushedFile.getFilePath() != null && pushedFile.getFilePath().contains("/")) {
            String folderPath = pushedFile.getFilePath().substring(0, pushedFile.getFilePath().lastIndexOf("/"));
            pushedFile.setFolderPath(folderPath);
        }

        return pushedFilesRepository.save(pushedFile);
    }
}
