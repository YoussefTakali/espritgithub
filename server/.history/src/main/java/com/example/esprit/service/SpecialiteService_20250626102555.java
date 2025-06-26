package com.example.esprit.service;

import com.example.esprit.model.Specialite;
import com.example.esprit.repository.SpecialiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SpecialiteService {

    private final SpecialiteRepository specialiteRepository;

    public Page<Specialite> getAllSpecialites(Pageable pageable) {
        return specialiteRepository.findAll(pageable);
    }

    public Optional<Specialite> getSpecialiteById(Long id) {
        return specialiteRepository.findById(id);
    }

    public Specialite saveSpecialite(Specialite specialite) {
        return specialiteRepository.save(specialite);
    }

    public void deleteSpecialite(Long id) {
        specialiteRepository.deleteById(id);
    }

    public Page<Specialite> searchSpecialites(String search, Pageable pageable) {
        return specialiteRepository.findBySpecialitesContainingIgnoreCase(search, pageable);
    }
}
