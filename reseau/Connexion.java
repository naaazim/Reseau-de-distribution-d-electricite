package com.example.reseau;

public class Connexion {
    private Maison maison;
    private Generateur generateur;
    
    public Connexion(Maison maison, Generateur generateur) {
        this.maison = maison;
        this.generateur = generateur;
    }
    public Maison getMaison() {
        return maison;
    }
    public Generateur getGenerateur() {
        return generateur;
    }
    public void afficher() {
        System.out.println(generateur.getNom() + " --> " + maison.getNom());
    }
  
}
