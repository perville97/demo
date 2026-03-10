package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "notes")
public class Note {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Etudiant etudiant;
    
    @ManyToOne
    @JoinColumn(name = "matiere_id", nullable = false)
    private Matiere matiere;
    
    @Column(nullable = false)
    private Double valeur;
    
    // Constructeurs
    public Note() {}
    
    public Note(Etudiant etudiant, Matiere matiere, Double valeur) {
        this.etudiant = etudiant;
        this.matiere = matiere;
        this.valeur = valeur;
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Etudiant getEtudiant() { return etudiant; }
    public void setEtudiant(Etudiant etudiant) { this.etudiant = etudiant; }
    
    public Matiere getMatiere() { return matiere; }
    public void setMatiere(Matiere matiere) { this.matiere = matiere; }
    
    public Double getValeur() { return valeur; }
    public void setValeur(Double valeur) { this.valeur = valeur; }
}