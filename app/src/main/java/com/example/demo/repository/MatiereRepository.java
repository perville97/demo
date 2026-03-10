package com.example.demo.repository;

import com.example.demo.entity.Matiere;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MatiereRepository extends JpaRepository<Matiere, Long> {
    
    @Query("SELECT m FROM Matiere m ORDER BY m.nom ASC")
    List<Matiere> findAllOrderByNom();
    
    boolean existsByCode(String code);
}