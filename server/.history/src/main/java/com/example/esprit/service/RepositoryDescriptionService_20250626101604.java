package com.example.esprit.service;

import com.example.esprit.model.RepositoryDescription;
import com.example.esprit.repository.RepositoryDescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RepositoryDescriptionService {

    private final RepositoryDescriptionRepository repositoryDescriptionRepository;

    public Optional<RepositoryDescription> getDescription(String owner, String repositoryName) {
        return repositoryDescriptionRepository.findByOwnerAndRepositoryName(owner, repositoryName);
    }

    public RepositoryDescription saveDescription(String owner, String repositoryName, String description, String username) {
        Optional<RepositoryDescription> existing = repositoryDescriptionRepository.findByOwnerAndRepositoryName(owner, repositoryName);
        
        if (existing.isPresent()) {
            RepositoryDescription repoDesc = existing.get();
            repoDesc.setDescription(description);
            repoDesc.setUpdatedBy(username);
            return repositoryDescriptionRepository.save(repoDesc);
        } else {
            RepositoryDescription repoDesc = new RepositoryDescription(owner, repositoryName, description, username);
            return repositoryDescriptionRepository.save(repoDesc);
        }
    }

    @Transactional
    public boolean deleteDescription(String owner, String repositoryName) {
        return repositoryDescriptionRepository.deleteByOwnerAndRepositoryName(owner, repositoryName) > 0;
    }

    public boolean exists(String owner, String repositoryName) {
        return repositoryDescriptionRepository.existsByOwnerAndRepositoryName(owner, repositoryName);
    }

    public List<RepositoryDescription> getDescriptionsByOwner(String owner) {
        return repositoryDescriptionRepository.findByOwnerOrderByUpdatedAtDesc(owner);
    }

    public List<RepositoryDescription> getDescriptionsCreatedBy(String username) {
        return repositoryDescriptionRepository.findByCreatedByOrderByCreatedAtDesc(username);
    }

    public List<RepositoryDescription> getDescriptionsUpdatedBy(String username) {
        return repositoryDescriptionRepository.findByUpdatedByOrderByUpdatedAtDesc(username);
    }

    public long countByOwner(String owner) {
        return repositoryDescriptionRepository.countByOwner(owner);
    }
}
