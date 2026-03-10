package com.example.demo.repository;

import com.example.demo.entity.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EtudiantRepository extends JpaRepository<Etudiant, Long> {
    
    @Query("SELECT e FROM Etudiant e ORDER BY e.nom ASC")
    List<Etudiant> findAllOrderByNom();
    
    boolean existsByMatricule(String matricule);
}