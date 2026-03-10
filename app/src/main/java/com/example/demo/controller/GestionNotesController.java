package com.example.demo.controller;

import com.example.demo.entity.Etudiant;
import com.example.demo.entity.Matiere;
import com.example.demo.entity.Note;
import com.example.demo.repository.EtudiantRepository;
import com.example.demo.repository.MatiereRepository;
import com.example.demo.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/")
public class GestionNotesController {
    
    @Autowired
    private EtudiantRepository etudiantRepository;
    
    @Autowired
    private MatiereRepository matiereRepository;
    
    @Autowired
    private NoteRepository noteRepository;
    
@GetMapping
public String index(Model model) {
    // Récupère les compteurs depuis la base de données
    long totalEtudiants = etudiantRepository.count();
    long totalMatieres = matiereRepository.count();
    long totalNotes = noteRepository.count();
    
    // Ajoute les statistiques au modèle
    model.addAttribute("totalEtudiants", totalEtudiants);
    model.addAttribute("totalMatieres", totalMatieres);
    model.addAttribute("totalNotes", totalNotes);
    
    return "index";
}
    
    @GetMapping("/etudiants")
    public String listEtudiants(Model model) {
        model.addAttribute("etudiants", etudiantRepository.findAll());
        model.addAttribute("etudiant", new Etudiant());
        return "etudiants";
    }
    
    @PostMapping("/etudiants/save")
    public String saveEtudiant(@ModelAttribute Etudiant etudiant, RedirectAttributes redirectAttributes) {
        try {
            etudiantRepository.save(etudiant);
            redirectAttributes.addFlashAttribute("success", "Étudiant ajouté avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'ajout : " + e.getMessage());
        }
        return "redirect:/etudiants";
    }
    
    @GetMapping("/cours")
    public String listCours(Model model) {
        model.addAttribute("matieres", matiereRepository.findAll());
        model.addAttribute("matiere", new Matiere());
        return "cours";
    }
    
    @PostMapping("/cours/save")
    public String saveMatiere(@ModelAttribute Matiere matiere, RedirectAttributes redirectAttributes) {
        try {
            matiereRepository.save(matiere);
            redirectAttributes.addFlashAttribute("success", "Matière ajoutée avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de l'ajout : " + e.getMessage());
        }
        return "redirect:/cours";
    }
    
    @GetMapping("/notes")
    public String listNotes(Model model) {
        model.addAttribute("etudiants", etudiantRepository.findAll());
        model.addAttribute("matieres", matiereRepository.findAll());
        model.addAttribute("notes", noteRepository.findAll());
        model.addAttribute("note", new Note());
        return "notes";
    }
    
    @PostMapping("/notes/save")
    public String saveNote(@RequestParam Long etudiantId, 
                          @RequestParam Long matiereId, 
                          @RequestParam Double valeur,
                          RedirectAttributes redirectAttributes) {
        try {
            Etudiant etudiant = etudiantRepository.findById(etudiantId)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé"));
            Matiere matiere = matiereRepository.findById(matiereId)
                .orElseThrow(() -> new RuntimeException("Matière non trouvée"));
            
            Note note = new Note(etudiant, matiere, valeur);
            noteRepository.save(note);
            redirectAttributes.addFlashAttribute("success", "Note ajoutée avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur : " + e.getMessage());
        }
        return "redirect:/notes";
    }
    
    public static class ResultatEtudiant {
        public Etudiant etudiant;
        public Double moyenne;
        
        public ResultatEtudiant(Etudiant etudiant, Double moyenne) {
            this.etudiant = etudiant;
            this.moyenne = moyenne;
        }
    }
    
    @GetMapping("/resultats")
    public String resultats(Model model) {
        List<Etudiant> etudiants = etudiantRepository.findAll();
        List<ResultatEtudiant> resultats = new ArrayList<>();
        
        for (Etudiant e : etudiants) {
            Double moyenne = calculerMoyenneEtudiant(e);
            resultats.add(new ResultatEtudiant(e, moyenne));
        }
        
        resultats.sort((a, b) -> Double.compare(b.moyenne, a.moyenne));
        
        Double moyenneClasse = resultats.stream()
            .mapToDouble(r -> r.moyenne)
            .average()
            .orElse(0.0);
        
        List<ResultatEtudiant> admis = resultats.stream()
            .filter(r -> r.moyenne >= 10)
            .collect(Collectors.toList());
        
        List<ResultatEtudiant> auDessusMoyenne = resultats.stream()
            .filter(r -> r.moyenne >= moyenneClasse)
            .collect(Collectors.toList());
        
        model.addAttribute("resultats", resultats);
        model.addAttribute("premier", resultats.isEmpty() ? null : resultats.get(0));
        model.addAttribute("dernier", resultats.isEmpty() ? null : resultats.get(resultats.size() - 1));
        model.addAttribute("moyenneClasse", String.format("%.2f", moyenneClasse));
        model.addAttribute("admis", admis);
        model.addAttribute("nombreAdmis", admis.size());
        model.addAttribute("auDessusMoyenne", auDessusMoyenne);
        
        return "resultats";
    }
    
    private Double calculerMoyenneEtudiantParMatiere(Etudiant etudiant, Matiere matiere) {
    // Utilise la nouvelle méthode qui retourne une List
    List<Note> notes = noteRepository.findAllByEtudiantAndMatiere(etudiant, matiere);
    
    if (notes.isEmpty()) return 0.0;
    
    return notes.stream()
        .mapToDouble(Note::getValeur)
        .average()
        .orElse(0.0);
}

// Page de sélection de matière
@GetMapping("/choisir-matiere")
public String choisirMatiere(Model model) {
    // Récupère TOUTES les matières depuis la base de données
    List<Matiere> matieres = matiereRepository.findAll();
    
    // Ajoute la liste au modèle pour Thymeleaf
    model.addAttribute("matieres", matieres);
    
    return "choisir-matiere"; // Le nom de ton fichier HTML
}

// Résultats pour une matière spécifique
@GetMapping("/resultats/{matiereId}")
public String resultatsParMatiere(@PathVariable Long matiereId, Model model) {
    // Récupère la matière
    Matiere matiere = matiereRepository.findById(matiereId)
        .orElseThrow(() -> new RuntimeException("Matière non trouvée"));
    
    // Récupère tous les étudiants
    List<Etudiant> etudiants = etudiantRepository.findAll();
    List<ResultatEtudiant> resultats = new ArrayList<>();
    
    // Calcule la moyenne pour chaque étudiant dans cette matière
    for (Etudiant e : etudiants) {
        Double moyenne = calculerMoyenneEtudiantParMatiere(e, matiere);
        resultats.add(new ResultatEtudiant(e, moyenne));
    }
    
    // Trie par moyenne décroissante
    resultats.sort((a, b) -> Double.compare(b.moyenne, a.moyenne));
    
    // Calcule la moyenne de la classe pour cette matière
    Double moyenneClasse = resultats.stream()
        .mapToDouble(r -> r.moyenne)
        .average()
        .orElse(0.0);
    
    // Filtre les admis (moyenne >= 10)
    List<ResultatEtudiant> admis = resultats.stream()
        .filter(r -> r.moyenne >= 10)
        .collect(Collectors.toList());
    
    // Étudiants au-dessus de la moyenne de la classe
    List<ResultatEtudiant> auDessusMoyenne = resultats.stream()
        .filter(r -> r.moyenne >= moyenneClasse)
        .collect(Collectors.toList());
    
    // Ajoute les attributs au modèle
    model.addAttribute("matiere", matiere);
    model.addAttribute("resultats", resultats);
    model.addAttribute("premier", resultats.isEmpty() ? null : resultats.get(0));
    model.addAttribute("dernier", resultats.isEmpty() ? null : resultats.get(resultats.size() - 1));
    model.addAttribute("moyenneClasse", String.format("%.2f", moyenneClasse));
    model.addAttribute("admis", admis);
    model.addAttribute("nombreAdmis", admis.size());
    model.addAttribute("auDessusMoyenne", auDessusMoyenne);
    
    return "resultats";
}


private Double calculerMoyenneEtudiant(Etudiant etudiant) {
    List<Note> notes = noteRepository.findByEtudiant(etudiant);
    
    if (notes.isEmpty()) return 0.0;
    
    // Regrouper les notes par matière
    Map<Long, List<Note>> notesParMatiere = notes.stream()
        .collect(Collectors.groupingBy(n -> n.getMatiere().getId()));
    
    double sommeNotesCoeff = 0.0;
    double sommeCoeff = 0.0;
    
    for (Map.Entry<Long, List<Note>> entry : notesParMatiere.entrySet()) {
        List<Note> notesMatiere = entry.getValue();
        Matiere matiere = notesMatiere.get(0).getMatiere();
        
        // Moyenne des notes pour cette matière
        double moyenneMatiere = notesMatiere.stream()
            .mapToDouble(Note::getValeur)
            .average()
            .orElse(0.0);
        
        sommeNotesCoeff += moyenneMatiere * matiere.getCoefficient();
        sommeCoeff += matiere.getCoefficient();
    }
    
    return sommeCoeff > 0 ? sommeNotesCoeff / sommeCoeff : 0.0;
}


}