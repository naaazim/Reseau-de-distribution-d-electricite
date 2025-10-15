package com.example.reseau;

import java.util.HashMap;
import java.util.Map;

public class Reseau {
    // Utilisation de HashMap pour un accès direct en O(1)
    private Map<String, Maison> ensembleMaisons;
    private Map<String, Generateur> ensembleGenerateurs;
    private Map<String, Connexion> ensembleConnexions; // clé = nom de la maison (unique)

    private int capaciteTotale;       // somme des capacités de tous les générateurs
    private int consommationTotale;   // somme des consommations de toutes les maisons

    public Reseau() {
        ensembleMaisons = new HashMap<>();
        ensembleGenerateurs = new HashMap<>();
        ensembleConnexions = new HashMap<>();
        capaciteTotale = 0;
        consommationTotale = 0;
    }

    // --- Getters ---
    public Map<String, Maison> getEnsembleMaisons() {
        return ensembleMaisons;
    }

    public Map<String, Generateur> getEnsembleGenerateurs() {
        return ensembleGenerateurs;
    }

    public Map<String, Connexion> getEnsembleConnexions() {
        return ensembleConnexions;
    }

    public int getCapaciteTotale() {
        return capaciteTotale;
    }

    public int getConsommationTotale() {
        return consommationTotale;
    }

    // --- Ajouter une maison au réseau ---
    public void ajouterMaison(Maison maison) {
        String nom = maison.getNom();
        int consoMaison = maison.getTypeConso().getConsommation();

        // Si la maison existe déjà → mise à jour
        if (ensembleMaisons.containsKey(nom)) {
            Maison m = ensembleMaisons.get(nom);
            System.out.println("La maison " + nom + " existe déjà.");

            // Si le type de consommation change → on ajuste la consommation totale
            if (m.getTypeConso() != maison.getTypeConso()) {
                consommationTotale -= m.getTypeConso().getConsommation();
                consommationTotale += consoMaison;
                m.setTypeConso(maison.getTypeConso());
                System.out.println("Mise à jour de sa consommation. Nouvelle consommation totale : " + consommationTotale + " kW.");
            }
            return;
        }

        // Vérifie la contrainte de capacité
        if (consommationTotale + consoMaison > capaciteTotale) {
            System.out.println("\nImpossible d’ajouter " + nom + " (" + consoMaison + " kW).");
            System.out.println("→ La consommation totale dépasserait la capacité du réseau (" + capaciteTotale + " kW).");
            return;
        }

        // Ajout de la maison
        ensembleMaisons.put(nom, maison);
        consommationTotale += consoMaison;
        System.out.println("Maison ajoutée : " + maison + " | Consommation totale : " + consommationTotale + " kW");
    }

    // --- Ajouter un générateur au réseau ---
    public void ajouterGenerateur(Generateur generateur) {
        String nom = generateur.getNom();
        int capacite = generateur.getCapacite();

        // Si le générateur existe déjà → mise à jour
        if (ensembleGenerateurs.containsKey(nom)) {
            Generateur g = ensembleGenerateurs.get(nom);
            System.out.println("Le générateur " + nom + " existe déjà.");

            if (g.getCapacite() != capacite) {
                capaciteTotale -= g.getCapacite();
                g.setCapacite(capacite);
                capaciteTotale += capacite;
                System.out.println("Mise à jour de sa capacité. Nouvelle capacité totale : " + capaciteTotale + " kW.");
            }
            return;
        }

        // Sinon ajout simple
        ensembleGenerateurs.put(nom, generateur);
        capaciteTotale += capacite;
        System.out.println("Générateur ajouté : " + generateur + " | Capacité totale du réseau : " + capaciteTotale + " kW");
    }

    // --- Ajouter une connexion au réseau ---
    public void ajouterConnexion(Connexion connexion) {
        String nomMaison = connexion.getMaison().getNom();
        String nomGenerateur = connexion.getGenerateur().getNom();

        // Vérifie existence des entités
        if (!ensembleMaisons.containsKey(nomMaison)) {
            System.out.println("La maison " + nomMaison + " n'existe pas dans le réseau.");
            return;
        }
        if (!ensembleGenerateurs.containsKey(nomGenerateur)) {
            System.out.println("Le générateur " + nomGenerateur + " n'existe pas dans le réseau.");
            return;
        }

        // Vérifie si la maison est déjà connectée
        if (ensembleConnexions.containsKey(nomMaison)) {
            System.out.println("La maison " + nomMaison + " est déjà connectée à un générateur.");
            return;
        }

        // Ajout de la connexion
        ensembleConnexions.put(nomMaison, connexion);
        System.out.println("Connexion ajoutée : " + nomMaison + " <-> " + nomGenerateur);
    }

    // --- Affichage des maisons ---
    public void afficherMaisons() {
        System.out.println("\n==== Ensemble des maisons ====");
        if (ensembleMaisons.isEmpty()) {
            System.out.println("Aucune maison dans le réseau.");
            return;
        }
        for (Maison m : ensembleMaisons.values()) {
            m.afficher();
        }
        System.out.println("Consommation totale : " + consommationTotale + " kW");
    }

    // --- Affichage des générateurs ---
    public void afficherGenerateurs() {
        System.out.println("\n==== Ensemble des générateurs ====");
        if (ensembleGenerateurs.isEmpty()) {
            System.out.println("Aucun générateur dans le réseau.");
            return;
        }
        for (Generateur g : ensembleGenerateurs.values()) {
            g.afficher();
        }
        System.out.println("Capacité totale du réseau : " + capaciteTotale + " kW");
    }

    // --- Affichage des connexions ---
    public void afficherConnexions() {
        System.out.println("\n==== Ensemble des connexions ====");
        if (ensembleConnexions.isEmpty()) {
            System.out.println("Aucune connexion dans le réseau.");
            return;
        }
        for (Connexion c : ensembleConnexions.values()) {
            c.afficher();
        }
    }
}
