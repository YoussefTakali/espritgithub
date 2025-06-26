package com.example.esprit.service;

import org.springframework.stereotype.Service;

@Service
public class RepositoryInfoService {
    
    private String repoOwner;
    private String repoName;
    private String branch;
    private String pusherName;

    public void setRepositoryInfo(String owner, String repo, String branch) {
        this.repoOwner = owner;
        this.repoName = repo;
        this.branch = branch;
    }

    public void setPusherName(String pusherName) {
        this.pusherName = pusherName;
    }

    public boolean isRepositoryConfigured() {
        return repoOwner != null && repoName != null && branch != null;
    }

    public String getRepoOwner() {
        return repoOwner;
    }

    public String getRepoName() {
        return repoName;
    }

    public String getBranch() {
        return branch;
    }

    public String getPusherName() {
        return pusherName;
    }
}
