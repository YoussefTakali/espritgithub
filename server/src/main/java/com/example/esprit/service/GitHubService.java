package com.example.esprit.service;

import com.example.esprit.dto.CreateRepoRequest;
import com.example.esprit.model.RepositoryEntity;
import com.example.esprit.repository.RepositoryEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitHubService {

    private final RestTemplate restTemplate;
    private final RepositoryEntityRepository repositoryEntityRepository;

    @Value("${github.token}")
    private String githubToken;

    private static final String GITHUB_API_URL = "https://api.github.com";

    public RepositoryEntity createRepository(CreateRepoRequest request) {
        try {
            // Create repository on GitHub
            String url = GITHUB_API_URL + "/user/repos";
            
            Map<String, Object> repoData = new HashMap<>();
            repoData.put("name", request.getName());
            repoData.put("description", request.getDescription());
            repoData.put("private", request.isPrivateRepo());
            repoData.put("auto_init", request.isAutoInit());
            
            if (request.getGitignoreTemplate() != null && !request.getGitignoreTemplate().isEmpty()) {
                repoData.put("gitignore_template", request.getGitignoreTemplate());
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(githubToken);
            headers.set("Accept", "application/vnd.github.v3+json");
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(repoData, headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                // Save to database
                RepositoryEntity repoEntity = RepositoryEntity.builder()
                        .repoName(request.getName())
                        .ownerName(request.getOwnerName())
                        .creationDate(new Date())
                        .classId(request.getClassId())
                        .build();
                
                return repositoryEntityRepository.save(repoEntity);
            } else {
                throw new RuntimeException("Failed to create repository on GitHub");
            }
            
        } catch (Exception e) {
            log.error("Error creating repository: {}", e.getMessage());
            throw new RuntimeException("Error creating repository: " + e.getMessage());
        }
    }

    public List<RepositoryEntity> getRepositoriesByClass(Long classId) {
        return repositoryEntityRepository.findByClassId(classId);
    }

    public List<RepositoryEntity> getRepositoriesByOwner(String ownerName) {
        return repositoryEntityRepository.findByOwnerName(ownerName);
    }

    public RepositoryEntity findByRepoNameAndOwner(String repoName, String ownerName) {
        return repositoryEntityRepository.findByRepoNameAndOwnerName(repoName, ownerName);
    }
}
