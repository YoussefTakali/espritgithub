package com.example.esprit.repository;

import com.example.esprit.model.RepositoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepositoryEntityRepository extends JpaRepository<RepositoryEntity, Long> {
    RepositoryEntity findByRepoNameAndOwnerName(String repoName, String ownerName);
    List<RepositoryEntity> findByClassId(Long classId);
    List<RepositoryEntity> findByOwnerName(String ownerName);
}
