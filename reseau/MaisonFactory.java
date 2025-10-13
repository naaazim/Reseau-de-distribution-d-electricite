package com.example.reseau;

import java.util.Scanner;

// Nous allons utiliser le Desing Pattern Factory pour créer des objets Maison à partir d'une saisie utilisateur sur une seule ligne
public class MaisonFactory {
    private Scanner scanner;

    public MaisonFactory() {
        this.scanner = new Scanner(System.in);
    }

    public Maison creerMaison(){
        System.out.print("Entrez le nom et le type de consommation (ex: M1 NORMALE) : ");
        String ligne = scanner.nextLine().trim(); // lit toute la ligne

        // On découpe la ligne en morceaux
        String[] parties = ligne.split(" "); // découpe par un ou plusieurs espaces

        if (parties.length != 2) {
            System.out.println("Format invalide. Exemple attendu : M1 NORMALE");
            return null;
        }

        String nom = parties[0];
        String typeStr = parties[1].toUpperCase();

        try {
            TypeConso type = TypeConso.valueOf(typeStr);
            return new Maison(nom, type);
        } catch (IllegalArgumentException e) {
            System.err.println("Type de consommation invalide. Utilisez BASSE, NORMALE ou FORTE.");
            return null;
        }
    }
}
