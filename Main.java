package com.example;

import java.util.Scanner;
import com.example.reseau.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Reseau reseau = new Reseau();
        MaisonFactory maisonFactory = new MaisonFactory();
        GenerateurFactory generateurFactory = new GenerateurFactory();

        int choix;

        do {
            // --- Si le réseau est vide (aucun générateur) ---
            if (reseau.estVide()) {
                System.err.println("\nVotre réseau est vide : ajoutez au moins un générateur pour commencer.");

                System.out.println("\n===== MENU INITIAL =====");
                System.out.println("1) Ajouter un générateur");
                System.out.println("2) Quitter");
                System.out.print("Votre choix : ");

                while (!scanner.hasNextInt()) {
                    System.out.println("Veuillez entrer un nombre valide !");
                    scanner.next();
                }
                choix = scanner.nextInt();
                scanner.nextLine(); // vide le buffer

                switch (choix) {
                    case 1 -> {
                        Generateur g = generateurFactory.creerGenerateur();
                        if (g != null)
                            reseau.ajouterGenerateur(g);
                    }
                    case 2 -> {
                        System.out.println("Fin du programme.");
                        scanner.close();
                        return;
                    }
                    default -> System.out.println("Choix invalide !");
                }
            }

            // --- Sinon : menu principal complet ---
            else {
                System.out.println("\n===== MENU PRINCIPAL =====");
                System.out.println("1) Ajouter un générateur");
                System.out.println("2) Ajouter une maison");
                System.out.println("3) Ajouter une connexion");
                System.out.println("4) Afficher le réseau");
                System.out.println("5) Quitter");
                System.out.print("Votre choix : ");

                while (!scanner.hasNextInt()) {
                    System.out.println("Veuillez entrer un nombre valide !");
                    scanner.next();
                }
                choix = scanner.nextInt();
                scanner.nextLine(); // vide le buffer

                switch (choix) {
                    // --- Ajouter un générateur ---
                    case 1 -> {
                        Generateur g = generateurFactory.creerGenerateur();
                        if (g != null)
                            reseau.ajouterGenerateur(g);
                    }

                    // --- Ajouter une maison ---
                    case 2 -> {
                        Maison m = maisonFactory.creerMaison();
                        if (m != null)
                            reseau.ajouterMaison(m);
                    }

                    // --- Ajouter une connexion ---
                    case 3 -> {
                        // Demander à l'utilisateur la maison et le générateur
                        System.out.print("Entrez la maison et le générateur à connecter (ex: M1 G1 ou G1 M1): ");
                        String ligne = scanner.nextLine().trim();
                        String[] parties = ligne.split(" ");

                        if (parties.length != 2) {
                            System.out.println("Format invalide. Exemple attendu : M1 G1");
                            break;
                        }

                        String nom1 = parties[0];
                        String nom2 = parties[1];

                        // Identifier les objets correspondants
                        Maison maison = reseau.getMaisonParNom(nom1);
                        Generateur generateur = reseau.getGenerateurParNom(nom2);

                        // Si l'ordre est inversé (ex: G1 M1)
                        if (maison == null && generateur == null) {
                            maison = reseau.getMaisonParNom(nom2);
                            generateur = reseau.getGenerateurParNom(nom1);
                        }

                        if (maison == null || generateur == null) {
                            System.out.println("Maison ou générateur introuvable. Vérifiez que les deux existent.");
                        } else {
                            reseau.ajouterConnexion(maison.getNom(), generateur.getNom());
                        }
                    }

                    // --- Afficher le réseau ---
                    case 4 -> reseau.afficherReseau();

                    // --- Quitter ---
                    case 5 -> {
                        System.out.println("Fin du programme.");
                        scanner.close();
                        return;
                    }

                    default -> System.out.println("Choix invalide !");
                }
            }

        } while (true);
    }
}
