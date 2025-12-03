package com.example.factory;

import com.example.reseau.Generateur;

import java.util.Scanner;

/**
 * Implémente le Design Pattern Factory pour créer des objets {@link Generateur}.
 * Cette factory utilise les entrées utilisateur via la console pour construire les objets.
 */
public class GenerateurFactory {
    private Scanner scanner;

    /**
     * Construit une nouvelle factory de générateurs.
     *
     * @param scanner Le scanner à utiliser pour lire les entrées utilisateur.
     */
    public GenerateurFactory(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Crée un nouvel objet {@link Generateur} à partir des entrées de l'utilisateur.
     * La méthode demande un nom et une capacité, valide l'entrée, et retourne le nouvel objet.
     *
     * @return Un nouvel objet {@code Generateur} si l'entrée est valide, sinon {@code null}.
     */
    public Generateur creerGenerateur() {
        System.out.print("Entrez le nom du générateur suivi de sa capacité en kW (ex: G1 60): ");
        String ligne = scanner.nextLine().trim();
        String[] parties = ligne.split("\\s+");

        if (parties.length != 2) {
            System.out.println("Format invalide. Exemple de saisie attendu : G1 60");
            return null;
        }

        String nom = parties[0];
        try {
            int capacite = Integer.parseInt(parties[1]);
            if (capacite < 0) {
                System.out.println("La capacité ne peut pas être négative.");
                return null;
            }
            return new Generateur(nom.toUpperCase(), capacite);
        } catch (NumberFormatException e) {
            System.out.println("La capacité doit être un nombre entier valide. Saisie invalide : '" + parties[1] + "'\n");
            return null;
        }
    }
}
