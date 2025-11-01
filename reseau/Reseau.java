package com.example.reseau;

import java.util.*;

public class Reseau {
    private Map<Generateur, List<Maison>> connexions; // Générateur → liste de maisons
    private List<Maison> maisonsNonConnectees;        // Maisons créées mais pas encore reliées
    private int capaciteTotale;
    private int consommationTotale;
    private static final int LAMBDA = 10; // Sévérité de la pénalisation 

    public Reseau() {
        //Utilisation de linkedHashMap pour conserver l'ordre d'ajout des éléments
        connexions = new LinkedHashMap<>();
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
                    System.out.println("⚠️  Mise à jour impossible : la capacité totale (" + nouvelleCapaciteTotale +
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
                        System.out.println("⚠️  Mise à jour impossible : la consommation totale (" + nouvelleTotale +
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
    public void afficher() {
        System.out.println("\n===== RÉSEAU ÉLECTRIQUE =====");

        if (connexions.isEmpty()) {
            System.out.println("Aucun générateur dans le réseau.");
            return;
        }

        for (Generateur g : connexions.keySet()) {
            List<Maison> maisons = connexions.get(g);
            System.out.println("");
            g.afficher();
            System.out.println(" alimente :");

            if (maisons.isEmpty()) {
                System.out.println("   Aucune maison connectée.");
            } else {
                for (Maison m : maisons) {
                    System.out.print("   - ");
                    m.afficher();
                }
            }
        }
        System.out.println("\nCapacité totale : " + capaciteTotale + " kW | Consommation totale : " + consommationTotale + " kW");
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
    public boolean isValide(){
        return maisonsNonConnectees.isEmpty();
    }
    public double getTauxUtilisation(Generateur g) {
        if (!connexions.containsKey(g)) {
            System.out.println("⚠️ Générateur " + g.getNom() + " non trouvé dans le réseau.");
            return 0;
        }

        int charge = 0;
        for (Maison m : connexions.get(g)) {
            charge += m.getTypeConso().getConsommation();
        }

        if (g.getCapacite() == 0) return 0; // éviter division par zéro
        return (double) charge / g.getCapacite();
    }
    public double dispersion() {
        if (connexions.isEmpty()) return 0;

        Map<Generateur, Double> taux = new HashMap<>();
        double somme = 0;

        for (Generateur g : connexions.keySet()) {
            double u = getTauxUtilisation(g);
            taux.put(g, u);
            somme += u;
        }

        double moyenne = somme / connexions.size();

        double dispersion = 0;
        for (double u : taux.values()) {
            dispersion += Math.abs(u - moyenne);
        }

        return dispersion;
    }
    public double surcharge(){
        if(connexions.isEmpty())return 0;
        double surcharge = 0;
        for(Generateur g : connexions.keySet()){
            double chargeActuelle = 0;
            for(Maison m : connexions.get(g)){
                chargeActuelle += m.getTypeConso().getConsommation();
            }
            surcharge += Math.max(0, (double)(chargeActuelle - g.getCapacite()) / g.getCapacite());
        }
        return surcharge;
    }

    public double calculerCout(){
        return dispersion() + LAMBDA * surcharge();
    }
    public void modifierConnexion(String ancienneMaison, String ancienGenerateur,String nouvelleMaison, String nouveauGenerateur) {
        Maison maison = getMaisonParNom(ancienneMaison);
        Generateur ancienGen = getGenerateurParNom(ancienGenerateur);
        Generateur nouveauGen = getGenerateurParNom(nouveauGenerateur);

        // Vérifier que la maison est bien connectée à l’ancien générateur
        if (!connexions.containsKey(ancienGen) || !connexions.get(ancienGen).contains(maison)) {
            System.out.println("⚠️  La maison " + maison.getNom() + " n'est pas connectée à " + ancienGen.getNom() + ".");
            return;
        }

        // Vérifier que le nouveau générateur existe dans le réseau
        if (!connexions.containsKey(nouveauGen)) {
            System.out.println("⚠️  Le générateur " + nouveauGenerateur + " n'existe pas dans le réseau.");
            return;
        }

        // Vérifier que la maison n’est pas déjà connectée à ce générateur
        if (connexions.get(nouveauGen).contains(maison)) {
            System.out.println("⚠️  La maison " + maison.getNom() + " est déjà connectée à " + nouveauGen.getNom() + ".");
            return;
        }

        // --- Modification effective ---
        connexions.get(ancienGen).remove(maison);
        connexions.get(nouveauGen).add(maison);

        System.out.println("✅ Connexion modifiée : " + maison.getNom() + 
                        " passe de " + ancienGen.getNom() + " à " + nouveauGen.getNom() + ".");
    }

    public Maison getMaisonDepuisLigne(String[] parts) {
        for (String s : parts) {
            Maison m = getMaisonParNom(s);
            if (m != null) return m;
        }
        return null;
    }

    public Generateur getGenerateurDepuisLigne(String[] parts) {
        for (String s : parts) {
            Generateur g = getGenerateurParNom(s);
            if (g != null) return g;
        }
        return null;
    }
}
