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
import org.junit.jupiter.api.Test;

public class Main {

    // --- Menu de calcul (réutilisé en mode fichier + mode manuel) ---bb
    private static void menuCalcul(Scanner scanner, Reseau reseau) {
        int choix;
        while (true) {
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
            scanner.nextLine(); // vide le buffer

            switch (choix) {
                case 1 -> {
                    System.out.print("Le coût de votre réseau est: ");
                    System.out.println(reseau.calculerCout());
                }
                case 2 -> {
                    System.out.print("Entrez la connexion à modifier (ex: M1 G1 ou G1 M1): ");
                    String ancienne = scanner.nextLine().trim();
                    String[] parts1 = ancienne.split("\\s+");
                    if (parts1.length != 2) {
                        System.out.println("Format invalide !");
                        break;
                    }

                    Maison ancienneMaison = reseau.getMaisonDepuisLigne(parts1);
                    Generateur ancienGen = reseau.getGenerateurDepuisLigne(parts1);

                    if (ancienneMaison == null || ancienGen == null) {
                        System.out.println("Connexion invalide (maison ou générateur introuvable).");
                        break;
                    }

                    System.out.print("Entrez la nouvelle connexion (ex: M1 G2 ou G2 M1): ");
                    String nouvelle = scanner.nextLine().trim();
                    String[] parts2 = nouvelle.split("\\s+");
                    if (parts2.length != 2) {
                        System.out.println("Format invalide !");
                        break;
                    }

                    Maison nouvelleMaison = reseau.getMaisonDepuisLigne(parts2);
                    Generateur nouveauGen = reseau.getGenerateurDepuisLigne(parts2);

                    if (nouvelleMaison == null || nouveauGen == null) {
                        System.out.println("Nouvelle connexion invalide (maison ou générateur introuvable).");
                        break;
                    }

                    reseau.modifierConnexion(
                            ancienneMaison.getNom(),
                            ancienGen.getNom(),
                            nouvelleMaison.getNom(),
                            nouveauGen.getNom()
                    );
                }
                case 3 -> {
                    reseau.afficher();
                }
                case 4 -> {
                    System.out.println("Fin du programme.");
                    return;
                }
                default -> System.out.println("Choix invalide !");
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Reseau reseau = new Reseau();

        // ============================
        // MODE FICHIER (PARTIE 2)
        // ============================
        if (args.length >= 1) {
            String path = args[0];
            try {
                reseau.chargerReseauDepuisFichier(path);
                System.out.println(" Réseau chargé depuis le fichier : " + path);
                reseau.afficher();
                menuCalcul(scanner, reseau);

            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Erreur lors du chargement du fichier (" + e.getMessage());
            } finally {
                scanner.close();
            }
            return; // on ne passe pas au mode manuel
        }

        // ============================
        // MODE MANUEL (PARTIE 1)
        // ============================
        MaisonFactory maisonFactory = new MaisonFactory();
        GenerateurFactory generateurFactory = new GenerateurFactory(scanner);

        int choix;

        do {
            System.out.println("\n===== MENU PRINCIPAL =====");
            System.out.println("1) Ajouter un générateur");
            System.out.println("2) Ajouter une maison");
            System.out.println("3) ajouter une connexion entre une maison et un générateur existants");
            System.out.println("4) supprimer une connexion existante entre une maison et un générateur");
            System.out.println("5) Quitter et passer au menu calcul (si réseau valide)");
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
                    if (!reseau.isConnexionPossible()) {

                        //Afficher les options de connexion disponibles (Générateurs + maisons non connectées)
                        reseau.afficherOptions();
                        // Demander à l'utilisateur la maison et le générateur
                        System.out.print("Entrez la maison et le générateur à connecter (ex: M1 G1 ou G1 M1): ");
                        String ligne = scanner.nextLine().trim();
                        String[] parties = ligne.split("\\s+");

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
                    } else {
                        System.out.println("Aucune connexion possible : vérifiez que vous avez au moins un générateur et une maison non connectée.");
                    }
                }

                // --- Supprimer une connexion ---
                case 4 -> {
                    System.out.print("Pour supprimer une connexion existante, entrez la maison et le générateur (ex: M1 G1 ou G1 M1): ");
                    String ligne = scanner.nextLine().trim();
                    String[] parties = ligne.split("\\s+");
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
                        reseau.supprimerConnexion(maison.getNom(), generateur.getNom());
                    }
                }

                // --- Vérifier que le réseau est valide, puis menu calcul ---
                case 5 -> {
                    if (reseau.isValide()) {
                        menuCalcul(scanner, reseau);
                        scanner.close();
                        return;
                    } else {
                        System.out.println("  Votre réseau n'est pas valide, vérifiez que toutes les maisons sont connectées");
                    }
                }

                default -> System.out.println("Choix invalide !");
            }
        } while (true);
    }
}
