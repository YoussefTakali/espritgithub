package com.example.esprit.repository;

import com.example.esprit.model.Repos;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReposRepository extends JpaRepository<Repos, Long> {
    @Query("SELECT r FROM Repos r WHERE r.classe.id = :classeId")
    List<Repos> findByClasseId(@Param("classeId") Long classeId);

    @Query("SELECT r FROM Repos r WHERE r.classe.id = :classeId")
    Page<Repos> findByClasseId(@Param("classeId") Long classeId, Pageable pageable);

    @Query("SELECT r FROM Repos r WHERE r.eleve.id = :eleveId")
    List<Repos> findByEleveId(@Param("eleveId") Long eleveId);

    @Query("SELECT r FROM Repos r WHERE r.classe.id = :classeId AND r.dateDebut BETWEEN :startDate AND :endDate")
    List<Repos> findByClasseIdAndDateRange(
            @Param("classeId") Long classeId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT r FROM Repos r WHERE r.classe.id = :classeId AND r.statut = :statut")
    List<Repos> findByClasseIdAndStatut(
            @Param("classeId") Long classeId,
            @Param("statut") String statut
    );
}
