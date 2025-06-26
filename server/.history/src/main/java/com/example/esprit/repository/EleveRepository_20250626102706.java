package com.example.esprit.repository;

import com.example.esprit.model.Eleve;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EleveRepository extends JpaRepository<Eleve, Long> {
    @Query("SELECT e FROM Eleve e WHERE e.classe.id = :classeId")
    List<Eleve> findByClasseId(@Param("classeId") Long classeId);

    @Query("SELECT e FROM Eleve e WHERE e.classe.id = :classeId")
    Page<Eleve> findByClasseId(@Param("classeId") Long classeId, Pageable pageable);

    @Query("SELECT e FROM Eleve e WHERE e.classe.id = :classeId AND (LOWER(e.nom) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(e.prenom) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(e.numeroEtudiant) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Eleve> findByClasseIdAndSearch(@Param("classeId") Long classeId, @Param("search") String search, Pageable pageable);
}
