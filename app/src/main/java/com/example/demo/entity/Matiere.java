package com.example.demo.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "matieres")
public class Matiere {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String code;
    
    @Column(nullable = false)
    private String nom;
    
    @Column(nullable = false)
    private Double coefficient;
    
    @OneToMany(mappedBy = "matiere", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Note> notes = new ArrayList<>();
    
    // Constructeurs
    public Matiere() {}
    
    public Matiere(String code, String nom, Double coefficient) {
        this.code = code;
        this.nom = nom;
        this.coefficient = coefficient;
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    
    public Double getCoefficient() { return coefficient; }
    public void setCoefficient(Double coefficient) { this.coefficient = coefficient; }
    
    public List<Note> getNotes() { return notes; }
    public void setNotes(List<Note> notes) { this.notes = notes; }
}