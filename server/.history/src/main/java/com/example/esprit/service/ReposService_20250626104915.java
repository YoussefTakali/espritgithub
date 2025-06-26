package com.example.esprit.service;

import com.example.esprit.model.Repos;
import com.example.esprit.repository.ReposRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReposService {

    private final ReposRepository reposRepository;

    public Repos createRepos(Repos repos) {
        return reposRepository.save(repos);
    }

    public Repos updateRepos(Long id, Repos repos) {
        Repos existingRepos = reposRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Repos not found with id: " + id));
        
        existingRepos.setDateDebut(repos.getDateDebut());
        existingRepos.setDateFin(repos.getDateFin());
        existingRepos.setMotif(repos.getMotif());
        existingRepos.setStatut(repos.getStatut());
        existingRepos.setEleve(repos.getEleve());
        existingRepos.setClasse(repos.getClasse());
        
        return reposRepository.save(existingRepos);
    }

    public void deleteRepos(Long id) {
        reposRepository.deleteById(id);
    }

    public List<Repos> getAllRepos() {
        return reposRepository.findAll();
    }

    public List<Repos> getReposByClasse(Long classeId) {
        return reposRepository.findByClasseId(classeId);
    }

    public List<Repos> getReposByEleve(Long eleveId) {
        return reposRepository.findByEleveId(eleveId);
    }

    public Page<Repos> getReposByClassePage(Long classeId, Pageable pageable) {
        return reposRepository.findByClasseId(classeId, pageable);
    }

    public Repos getReposById(Long id) {
        return reposRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Repos not found with id: " + id));
    }

    public List<Repos> getReposByClasseAndDateRange(Long classeId, LocalDateTime startDate, LocalDateTime endDate) {
        return reposRepository.findByClasseIdAndDateRange(classeId, startDate, endDate);
    }

    public List<Repos> getReposByClasseAndStatut(Long classeId, String statut) {
        return reposRepository.findByClasseIdAndStatut(classeId, statut);
    }
}
