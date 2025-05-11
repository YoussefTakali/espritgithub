package com.example.gitbackend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Commit {
    @Id
    private String sha; // Commit SHA as unique ID

    private String branch;
    private String message;
    private String authorName;
    private String authorLogin;
    private String authorAvatarUrl;
    private String date;
    private  String RepoName;

    public void setRepoName(String repoName) {
        RepoName = repoName;
    }

    public String getSha() {

        return sha;
    }

    public String getRepoName() {
        return RepoName;
    }

    public void setSha(String sha) {
        this.sha = sha;
    }
    public String getBranch() {
        return branch;
    }
    public void setBranch(String branch) {
        this.branch = branch;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getAuthorName() {
        return authorName;
    }
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }
    public String getAuthorLogin() {
        return authorLogin;
    }
    public void setAuthorLogin(String authorLogin) {
        this.authorLogin = authorLogin;
    }
    public String getAuthorAvatarUrl() {
        return authorAvatarUrl;
    }
    public void setAuthorAvatarUrl(String authorAvatarUrl) {
        this.authorAvatarUrl = authorAvatarUrl;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    @ManyToOne
    @JoinColumn(name = "repository_id")
    private RepositoryEntity repository;

    public void setRepository(RepositoryEntity repoEntity) {
        repository = repoEntity;
    }
}
