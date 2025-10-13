package com.example.reseau;

import java.util.ArrayList;
import java.util.List;

public class Reseau {
    private List<Maison> ensembleMaisons;
    private List<Generateur> ensembleGenerateurs;
    private List<Connexion> ensembleConnexions;

    public Reseau(){
        ensembleMaisons = new ArrayList<>();
        ensembleGenerateurs = new ArrayList<>();
        ensembleConnexions = new ArrayList<>();
    }

    public List<Maison> getEnsembleMaisons() {
        return ensembleMaisons;
    }

    public List<Generateur> getEnsembleGenerateurs() {
        return ensembleGenerateurs;
    }

    public List<Connexion> getEnsembleConnexions() {
        return ensembleConnexions;
    }

    // Ajouter une maison au réseau ou mettre à jour sa consommation si elle existe déjà
    public void ajouterMaison(Maison maison){
        // Vérifie si la maison est déjà présente dans la liste
        if(ensembleMaisons.contains(maison)){
            // Récupère la maison existante grâce à son index
            int index = ensembleMaisons.indexOf(maison);
            Maison m = ensembleMaisons.get(index);

            // Message d’avertissement
            System.out.println("La maison " + maison.getNom() + " existe déjà");

            // Si le type de consommation est différent, on le met à jour
            if(m.getTypeConso() != maison.getTypeConso()){
                System.out.println("Mise à jour de sa consommation");
                m.setTypeConso(maison.getTypeConso());
            }
        }else{
            // Sinon, on ajoute la nouvelle maison à la liste
            ensembleMaisons.add(maison);
        }
    }

    // Ajouter un générateur au réseau ou mettre à jour sa capacité si il existe déjà
    public void ajouterGenerateur(Generateur generateur){
        if(ensembleGenerateurs.contains(generateur)){
            int index = ensembleGenerateurs.indexOf(generateur);
            Generateur g = ensembleGenerateurs.get(index);
             // Message d’avertissement
            System.out.println("Le générateur " + generateur.getNom() + " existe déjà");
            if(g.getCapacite() != generateur.getCapacite()){
                System.out.println("Mise à jour de la capacité");
                g.setCapacite(generateur.getCapacite());
            }
        }else{
            ensembleGenerateurs.add(generateur);
        }
    }

    public void ajouterConnexion(Connexion connexion) {
        if (!ensembleGenerateurs.contains(connexion.getGenerateur())) {
            System.out.println("Le générateur " + connexion.getGenerateur().getNom() + " n'existe pas dans le réseau.");
            return;
        }
        if (!ensembleMaisons.contains(connexion.getMaison())) {
            System.out.println("La maison " + connexion.getMaison().getNom() + " n'existe pas dans le réseau.");
            return;
        }

        // Vérifie si la maison est déjà connectée
        for (Connexion c : ensembleConnexions) {
            if (c.getMaison().equals(connexion.getMaison())) {
                System.out.println("La maison " + connexion.getMaison().getNom() + " est déjà connectée à un générateur.");
                return;
            }
        }

        // Si tout est correct, on ajoute la connexion
        ensembleConnexions.add(connexion);
        System.out.println("Connexion ajoutée : " + connexion.getMaison().getNom() + " ↔ " + connexion.getGenerateur().getNom());
    }


    //Affichage des connexions du réseau
    public void afficherConnexions(){
        System.out.println("==== Ensemble des connexion ====");
        if(ensembleConnexions.isEmpty())System.out.println("Aucune connexion dans le réseau");
        else{
            for (Connexion connexion : ensembleConnexions){
                connexion.afficher();
            }
        }
    }
    //Affichage des générateurs du réseau
    public void afficherGenerateurs(){
        System.out.println("==== Ensemble des générateurs ====");
        if(ensembleGenerateurs.isEmpty())System.out.println("Aucun générateur dans le réseau");
        else{
            for (Generateur generateur : ensembleGenerateurs){
                generateur.afficher();
            }
        }
    }
    //Affichage des maisons du réseau
    public void afficherMaisons(){
        System.out.println("==== Ensemble des maisons ====");
        if(ensembleMaisons.isEmpty())System.out.println("Aucune maison dans le réseau");
        else{
            for (Maison maison : ensembleMaisons){
                maison.afficher();
            }
        }
    }
}
