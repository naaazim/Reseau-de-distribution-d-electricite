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
            // Vérifie si le réseau contient au moins une maison et un générateur
            if (reseau.getEnsembleGenerateurs().isEmpty() || reseau.getEnsembleMaisons().isEmpty()) {

                if (reseau.getEnsembleGenerateurs().isEmpty() && reseau.getEnsembleMaisons().isEmpty()) {
                    System.err.println("\nVotre réseau est vide : ajoutez au moins un générateur et une maison.");
                } else if (reseau.getEnsembleGenerateurs().isEmpty()) {
                    System.err.println("\nIl manque un générateur dans le réseau. Ajoutez-en un avant de continuer.");
                } else if (reseau.getEnsembleMaisons().isEmpty()) {
                    System.err.println("\nIl manque une maison dans le réseau. Ajoutez-en une avant de continuer.");
                }

                // Menu réduit tant que le réseau n'est pas valide
                System.out.println("\n===== MENU INITIAL =====");
                System.out.println("1) Ajouter un générateur");
                System.out.println("2) Ajouter une maison");
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
                        if (g != null) reseau.ajouterGenerateur(g);
                    }
                    case 2 -> {
                        Maison m = maisonFactory.creerMaison();
                        if (m != null) reseau.ajouterMaison(m);
                    }
                    default -> System.out.println("Choix invalide !");
                }

            } else {
                // Menu complet (réseau valide)
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
                    case 1 -> {
                        Generateur g = generateurFactory.creerGenerateur();
                        if (g != null) reseau.ajouterGenerateur(g);
                    }

                    case 2 -> {
                        Maison m = maisonFactory.creerMaison();
                        if (m != null) reseau.ajouterMaison(m);
                    }

                    case 3 -> {
                        System.out.println("Entrez la maison et le générateur à connecter (ex: M1 G1 ou G1 M1) : ");
                        String ligne = scanner.nextLine().trim();
                        String[] parties = ligne.split(" ");

                        if (parties.length != 2) {
                            System.out.println("Format invalide. Exemple attendu : M1 G1");
                            break;
                        }

                        // Accès direct via les HashMaps
                        Maison maison = reseau.getEnsembleMaisons().get(parties[0]);
                        Generateur generateur = reseau.getEnsembleGenerateurs().get(parties[1]);

                        // Si inversé (G1 M1)
                        if (maison == null && generateur == null) {
                            maison = reseau.getEnsembleMaisons().get(parties[1]);
                            generateur = reseau.getEnsembleGenerateurs().get(parties[0]);
                        }

                        if (maison == null || generateur == null) {
                            System.out.println("Maison ou générateur introuvable. Vérifiez que les deux existent.");
                        } else {
                            reseau.ajouterConnexion(new Connexion(maison, generateur));
                        }
                    }

                    case 4 -> {
                        System.out.println("\n===== AFFICHAGE DU RÉSEAU =====");
                        reseau.afficherGenerateurs();
                        reseau.afficherMaisons();
                        reseau.afficherConnexions();
                    }

                    case 5 -> System.out.println("👋 Fin du programme. À bientôt !");

                    default -> System.out.println("Choix invalide !");
                }
            }
        } while (choix != 5);

        scanner.close();
    }
}
