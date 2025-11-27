package com.example.factory;

import com.example.reseau.Generateur;

import java.util.Scanner;

//Nous allons utiliser le Design Pattern Factory pour créer de nouveaux objets de type Generateur grâce à une saisie utilisteur 
public class GenerateurFactory {
    private Scanner scanner;

    public GenerateurFactory(Scanner scanner) {
        this.scanner = scanner;
    }

    public Generateur creerGenerateur() {
        System.out.println("Entrez le nom du générateur suivi de sa capacité en kW (ex: G1 60): ");
        String ligne = scanner.nextLine().trim();
        String[] parties = ligne.split("\\s+");

        if (parties.length != 2) {
            System.err.println("Format invalide. Exemple de saisie attendu : G1 60");
            return null;
        }

        String nom = parties[0];
        try {
            int capacite = Integer.parseInt(parties[1]);
            if (capacite < 0) {
                System.err.println("La capacité ne peut pas être négative.");
                return null;
            }
            return new Generateur(nom.toUpperCase(), capacite);
        } catch (NumberFormatException e) {
            System.err.println("La capacité doit être un nombre entier valide. Saisie invalide : '" + parties[1] + "'");
            return null;
        }
    }
}
