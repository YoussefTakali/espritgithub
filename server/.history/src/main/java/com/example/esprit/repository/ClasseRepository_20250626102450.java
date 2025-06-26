package com.example.esprit.repository;

import com.example.esprit.model.Classe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClasseRepository extends JpaRepository<Classe, Long> {
    @Query("SELECT c FROM Classe c WHERE c.niveau.id = :niveauId")
    List<Classe> findByNiveauId(@Param("niveauId") Long niveauId);

    @Query("SELECT c FROM Classe c WHERE c.niveau.id = :niveauId")
    Page<Classe> findByNiveauId(@Param("niveauId") Long niveauId, Pageable pageable);

    @Query("SELECT c FROM Classe c WHERE c.niveau.id = :niveauId AND LOWER(c.number) LIKE LOWER(CONCAT('%', :number, '%'))")
    Page<Classe> findByNiveauIdAndNumberContainingIgnoreCase(@Param("niveauId") Long niveauId, @Param("number") String number, Pageable pageable);
}
