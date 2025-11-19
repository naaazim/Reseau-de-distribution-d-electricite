/*
 * GROUPE: JEUDI MATIN
 * MEMBRES:
 *      - HAMIA Abderahmane Nazim
 *      - FERHANI Ales Amazigh
 *      - BENOUFELLA Mohamed Yacine
 */
package main.java.com.example;

import java.util.Scanner;
import main.java.com.example.reseau.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Reseau reseau = new Reseau();
        MaisonFactory maisonFactory = new MaisonFactory();
        GenerateurFactory generateurFactory = new GenerateurFactory(scanner);

        // ===============================================
        //        PARTIE 2 : CHARGEMENT AUTOMATIQUE
        // ===============================================
        if (args.length > 0) {
            String chemin = args[0];
            try {
                reseau.chargerReseauDepuisFichier(chemin);
                System.out.println("Réseau chargé automatiquement depuis : " + chemin);

                // Après chargement → aller directement au menu calcul
                lancerMenuCalcul(scanner, reseau);
                scanner.close();
                return;

            } catch (Exception e) {
                System.out.println("Erreur lors du chargement du fichier : " + e.getMessage());
            }
        }

        // Sinon → PARTIE 1 (menu manuel)
        int choix;

        do {
            System.out.println("\n===== MENU PRINCIPAL =====");
            System.out.println("1) Ajouter un générateur");
            System.out.println("2) Ajouter une maison");
            System.out.println("3) ajouter une connexion entre une maison et un générateur existants");
            System.out.println("4) supprimer une connexion existante entre une maison et un générateur");
            System.out.println("5) Quitter");
            System.out.print("Votre choix : ");

            while (!scanner.hasNextInt()) {
                System.out.println("Veuillez entrer un nombre valide !");
                scanner.next();
            }
            choix = scanner.nextInt();
            scanner.nextLine();

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
                    if (!reseau.isConnexionPossible()) {
                        reseau.afficherOptions();
                        System.out.print("Entrez la maison et le générateur à connecter : ");
                        String[] parties = scanner.nextLine().trim().split("\\s+");

                        if (parties.length != 2) {
                            System.out.println("Format invalide !");
                            break;
                        }

                        String nom1 = parties[0];
                        String nom2 = parties[1];

                        Maison maison = reseau.getMaisonParNom(nom1);
                        Generateur gen = reseau.getGenerateurParNom(nom2);

                        if (maison == null && gen == null) {
                            maison = reseau.getMaisonParNom(nom2);
                            gen = reseau.getGenerateurParNom(nom1);
                        }

                        if (maison == null || gen == null) {
                            System.out.println("Erreur : maison ou générateur introuvable.");
                        } else {
                            reseau.ajouterConnexion(maison.getNom(), gen.getNom());
                        }
                    } else {
                        System.out.println("Aucune connexion possible.");
                    }
                }
                case 4 -> {
                    System.out.print("Entrez la maison et le générateur : ");
                    String[] parties = scanner.nextLine().trim().split("\\s+");
                    if (parties.length != 2) {
                        System.out.println("Format invalide !");
                        break;
                    }

                    String nom1 = parties[0];
                    String nom2 = parties[1];

                    Maison maison = reseau.getMaisonParNom(nom1);
                    Generateur gen = reseau.getGenerateurParNom(nom2);

                    if (maison == null && gen == null) {
                        maison = reseau.getMaisonParNom(nom2);
                        gen = reseau.getGenerateurParNom(nom1);
                    }

                    if (maison == null || gen == null) {
                        System.out.println("Erreur : introuvable.");
                    } else {
                        reseau.supprimerConnexion(maison.getNom(), gen.getNom());
                    }
                }
                case 5 -> {
                    if (reseau.isValide()) {
                        lancerMenuCalcul(scanner, reseau);
                        scanner.close();
                        return;
                    } else {
                        System.out.println("⚠️ Le réseau n'est pas valide !");
                    }
                }
                default -> System.out.println("Choix invalide !");
            }
        } while (true);
    }

    // ============================================================
    //                SOUS-MENU CALCUL (inchangé)
    // ============================================================
    private static void lancerMenuCalcul(Scanner scanner, Reseau reseau) {
        int choix;
        do {
            System.out.println("\n===== MENU CALCUL =====");
            System.out.println("1) Calculer le coût du réseau électrique actuel");
            System.out.println("2) Modifier une connexion");
            System.out.println("3) Afficher le réseau");
            System.out.println("4) Fin");
            System.out.print("Votre choix : ");

            while (!scanner.hasNextInt()) {
                System.out.println("Veuillez entrer un nombre valide !");
                scanner.next();
            }

            choix = scanner.nextInt();
            scanner.nextLine();

            switch (choix) {
                case 1 -> {
                    System.out.println("Coût : " + reseau.calculerCout());
                }
                case 2 -> {
                    System.out.print("Entrez la connexion à modifier : ");
                    String[] parts1 = scanner.nextLine().trim().split("\\s+");
                    if (parts1.length != 2) {
                        System.out.println("Format invalide !");
                        break;
                    }

                    Maison ancienneMaison = reseau.getMaisonDepuisLigne(parts1);
                    Generateur ancienGen = reseau.getGenerateurDepuisLigne(parts1);

                    if (ancienneMaison == null || ancienGen == null) {
                        System.out.println("Connexion introuvable.");
                        break;
                    }

                    System.out.print("Nouvelle connexion : ");
                    String[] parts2 = scanner.nextLine().trim().split("\\s+");
                    if (parts2.length != 2) {
                        System.out.println("Format invalide !");
                        break;
                    }

                    Maison nouvelleMaison = reseau.getMaisonDepuisLigne(parts2);
                    Generateur nouveauGen = reseau.getGenerateurDepuisLigne(parts2);

                    if (nouvelleMaison == null || nouveauGen == null) {
                        System.out.println("Nouvelle connexion invalide.");
                        break;
                    }

                    reseau.modifierConnexion(
                            ancienneMaison.getNom(),
                            ancienGen.getNom(),
                            nouvelleMaison.getNom(),
                            nouveauGen.getNom()
                    );
                }
                case 3 -> reseau.afficher();
                case 4 -> {
                    System.out.println("Fin du programme.");
                    return;
                }
                default -> System.out.println("Choix invalide !");
            }

        } while (true);
    }
}
