package com.example.esprit.repository;

import com.example.esprit.model.Specialite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecialiteRepository extends JpaRepository<Specialite, Long> {
    Page<Specialite> findBySpecialitesContainingIgnoreCase(String search, Pageable pageable);
}
