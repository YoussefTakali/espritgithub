package com.example.esprit.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "pushed_files")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PushedFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "repo_name")
    private String repoName;

    @Column(name = "commit_id")
    private String commitId;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "folder_path")
    private String folderPath;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "file_extension")
    private String fileExtension;

    @Column(name = "branch")
    private String branch;

    @Column(name = "pusher_name")
    private String pusherName;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "pushed_at")
    private LocalDateTime pushedAt = LocalDateTime.now();

    @Column(name = "commit_message")
    private String commitMessage;
}
