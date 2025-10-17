package com.example.reseau;

import java.util.*;

public class Reseau {
    private Map<Generateur, List<Maison>> connexions; // Générateur → liste de maisons
    private List<Maison> maisonsNonConnectees;        // Maisons créées mais pas encore reliées
    private int capaciteTotale;
    private int consommationTotale;

    public Reseau() {
        connexions = new HashMap<>();
        maisonsNonConnectees = new ArrayList<>();
        capaciteTotale = 0;
        consommationTotale = 0;
    }

    // --- Ajouter ou mettre à jour un générateur ---
    public void ajouterGenerateur(Generateur g) {
        for (Generateur existant : connexions.keySet()) {
            if (existant.getNom().equalsIgnoreCase(g.getNom())) {
                System.out.println("Le générateur " + g.getNom() + " existe déjà.");

                int ancienneCapacite = existant.getCapacite();
                int nouvelleCapacite = g.getCapacite();
                int nouvelleCapaciteTotale = capaciteTotale - ancienneCapacite + nouvelleCapacite;

                if (nouvelleCapaciteTotale < consommationTotale) {
                    System.out.println("Mise à jour impossible : la capacité totale (" + nouvelleCapaciteTotale +
                            " kW) serait inférieure à la consommation actuelle (" + consommationTotale + " kW).");
                    return;
                }

                existant.setCapacite(nouvelleCapacite);
                capaciteTotale = nouvelleCapaciteTotale;
                System.out.println("Capacité du générateur " + existant.getNom() +
                        " mise à jour à " + nouvelleCapacite + " kW.");
                System.out.println("Capacité totale du réseau : " + capaciteTotale + " kW.");
                return;
            }
        }

        // Ajout d’un nouveau générateur
        connexions.put(g, new ArrayList<>());
        capaciteTotale += g.getCapacite();
        System.out.println("Générateur ajouté : " + g + " | Capacité totale du réseau : " + capaciteTotale + " kW");
    }

    // --- Ajouter ou mettre à jour une maison (non connectée) ---
    public void ajouterMaison(Maison m) {
        // Vérifie si la maison est déjà connectée
        for (List<Maison> liste : connexions.values()) {
            for (Maison existante : liste) {
                if (existante.getNom().equalsIgnoreCase(m.getNom())) {
                    System.out.println("La maison " + m.getNom() + " existe déjà et est connectée.");

                    int ancienneConso = existante.getTypeConso().getConsommation();
                    int nouvelleConso = m.getTypeConso().getConsommation();
                    int nouvelleTotale = consommationTotale - ancienneConso + nouvelleConso;

                    if (nouvelleTotale > capaciteTotale) {
                        System.out.println("Mise à jour impossible : la consommation totale (" + nouvelleTotale +
                                " kW) dépasserait la capacité du réseau (" + capaciteTotale + " kW).");
                        return;
                    }

                    existante.setTypeConso(m.getTypeConso());
                    consommationTotale = nouvelleTotale;
                    System.out.println("Mise à jour de la consommation de " + m.getNom() +
                            ". Nouvelle consommation totale : " + consommationTotale + " kW.");
                    return;
                }
            }
        }

        // Vérifie si elle existe parmi les maisons non connectées
        for (Maison existante : maisonsNonConnectees) {
            if (existante.getNom().equalsIgnoreCase(m.getNom())) {
                System.out.println("La maison " + m.getNom() + " existe déjà (non connectée).");
                existante.setTypeConso(m.getTypeConso());
                System.out.println("Type de consommation mis à jour : " + m.getTypeConso());
                return;
            }
        }

        // Sinon, nouvelle maison
        maisonsNonConnectees.add(m);
        System.out.println("Maison ajoutée : " + m + " (non connectée)");
    }

    // --- Ajouter une connexion (relier une maison à un générateur) ---
    public void ajouterConnexion(String nomMaison, String nomGenerateur) {
        Generateur g = getGenerateurParNom(nomGenerateur);
        if (g == null) {
            System.out.println("Le générateur " + nomGenerateur + " n'existe pas.");
            return;
        }

        Maison m = getMaisonParNom(nomMaison);
        if (m == null) {
            for (Maison tmp : maisonsNonConnectees) {
                if (tmp.getNom().equalsIgnoreCase(nomMaison)) {
                    m = tmp;
                    break;
                }
            }
            if (m == null) {
                System.out.println("La maison " + nomMaison + " n'existe pas. Veuillez d'abord l'ajouter.");
                return;
            }
        }

        // Vérifie si la maison est déjà connectée
        for (List<Maison> liste : connexions.values()) {
            if (liste.contains(m)) {
                System.out.println("La maison " + nomMaison + " est déjà connectée à un générateur.");
                return;
            }
        }

        int nouvelleConso = consommationTotale + m.getTypeConso().getConsommation();
        if (nouvelleConso > capaciteTotale) {
            System.out.println("Connexion impossible : la consommation totale (" + nouvelleConso +
                    " kW) dépasserait la capacité totale (" + capaciteTotale + " kW).");
            return;
        }

        connexions.get(g).add(m);
        maisonsNonConnectees.remove(m);
        consommationTotale = nouvelleConso;
        System.out.println("Connexion ajoutée : " + g.getNom() + " → " + m.getNom() +
                " | Consommation totale : " + consommationTotale + " kW");
    }

    // --- Afficher le réseau complet ---
    public void afficherReseau() {
        System.out.println("\n===== RÉSEAU ÉLECTRIQUE =====");

        if (connexions.isEmpty()) {
            System.out.println("Aucun générateur dans le réseau.");
            return;
        }

        for (Generateur g : connexions.keySet()) {
            List<Maison> maisons = connexions.get(g);
            System.out.println("\n" + g.getNom() + " (" + g.getCapacite() + " kW) alimente :");

            if (maisons.isEmpty()) {
                System.out.println("   Aucune maison connectée.");
            } else {
                for (Maison m : maisons) {
                    System.out.println("   - " + m.getNom() + " (" + m.getTypeConso() + " - " +
                            m.getTypeConso().getConsommation() + " kW)");
                }
            }
        }

        if (!maisonsNonConnectees.isEmpty()) {
            System.out.println("\nMaisons non connectées :");
            for (Maison m : maisonsNonConnectees) {
                System.out.println("   - " + m.getNom() + " (" + m.getTypeConso() + ")");
            }
        }

        System.out.println("\nCapacité totale : " + capaciteTotale + " kW | Consommation totale : " + consommationTotale + " kW");
    }

    // --- Méthodes utilitaires ---
    public boolean estVide() {
        return connexions.isEmpty();
    }

    public Generateur getGenerateurParNom(String nom) {
        for (Generateur g : connexions.keySet()) {
            if (g.getNom().equalsIgnoreCase(nom)) return g;
        }
        return null;
    }

    public Maison getMaisonParNom(String nom) {
        for (List<Maison> liste : connexions.values()) {
            for (Maison m : liste) {
                if (m.getNom().equalsIgnoreCase(nom)) return m;
            }
        }
        for (Maison m : maisonsNonConnectees) {
            if (m.getNom().equalsIgnoreCase(nom)) return m;
        }
        return null;
    }
}
