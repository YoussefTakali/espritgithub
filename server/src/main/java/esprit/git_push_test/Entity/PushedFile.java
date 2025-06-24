package esprit.git_push_test.Entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "pushed_files")
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRepoName() {
        return repoName;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getPushedAt() {
        return pushedAt;
    }

    public void setPushedAt(LocalDateTime pushedAt) {
        this.pushedAt = pushedAt;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getPusherName() {
        return pusherName;
    }

    public void setPusherName(String pusherName) {
        this.pusherName = pusherName;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public String getCommitMessage() {
        return commitMessage;
    }

    public void setCommitMessage(String commitMessage) {
        this.commitMessage = commitMessage;
    }
}
