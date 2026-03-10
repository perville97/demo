package com.example.demo.repository;

import com.example.demo.entity.Note;
import com.example.demo.entity.Etudiant;
import com.example.demo.entity.Matiere;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    
    List<Note> findByEtudiant(Etudiant etudiant);
    
    List<Note> findByMatiere(Matiere matiere);
    
    // ✅ Pour avoir TOUTES les notes (utilisé dans les moyennes)
    List<Note> findAllByEtudiantAndMatiere(Etudiant etudiant, Matiere matiere);
    
    // ⚠️ Pour avoir UNE seule note (si nécessaire)
    Optional<Note> findByEtudiantAndMatiere(Etudiant etudiant, Matiere matiere);
    
    @Query("SELECT AVG(n.valeur) FROM Note n WHERE n.etudiant = :etudiant")
    Double findMoyenneByEtudiant(@Param("etudiant") Etudiant etudiant);
    
    @Query("SELECT n.etudiant, AVG(n.valeur) as moyenne FROM Note n GROUP BY n.etudiant ORDER BY moyenne DESC")
    List<Object[]> findMoyennesTousEtudiants();
    
    boolean existsByEtudiantAndMatiere(Etudiant etudiant, Matiere matiere);
}