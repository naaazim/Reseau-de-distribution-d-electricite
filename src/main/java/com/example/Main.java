/*
 * GROUPE: JEUDI MATIN
 * MEMBRES:
 *      - HAMIA Abderahmane Nazim
 *      - FERHANI Ales Amazigh
 *      - BENOUFELLA Mohamed Yacine
 */
package com.example;

import java.util.Scanner;
import java.io.IOException;

import com.example.factory.GenerateurFactory;
import com.example.factory.MaisonFactory;
import com.example.reseau.*;

public class Main {

    /**
     * Méthode sécurisée pour demander un entier à l'utilisateur.
     * Boucle jusqu'à ce qu'un entier unique et valide soit entré.
     * @param scanner Le scanner à utiliser.
     * @return L'entier validé.
     */
    private static int askForInt(Scanner scanner) {
        String line = scanner.nextLine().trim();
        
        if (line.isEmpty()) {
            System.out.println("\nEntrée invalide. Aucune valeur n'a été saisie.");
            return -1;
        }

        String[] parts = line.split("\\s+");
        if (parts.length != 1) {
            System.out.println("\nEntrée invalide. Veuillez entrer un seul nombre entier.");
            return -1;
        }

        try {
            return Integer.parseInt(parts[0]);
        } catch (NumberFormatException e) {
            System.out.println("\nEntrée invalide. '" + parts[0] + "' n'est pas un nombre entier valide.");
            return -1;
        }
    }

    // --- Menu de calcul (réutilisé en mode fichier + mode manuel)
    /**
     * Affiche le menu de calcul et gère les interactions de l'utilisateur.
     * Ce menu permet de :
     * - Calculer le coût total du réseau.
     * - Modifier une connexion existante entre une maison et un générateur.
     * - Afficher la configuration actuelle du réseau.
     * - Quitter le sous-menu.
     *
     * @param scanner L'objet {@link Scanner} pour lire l'entrée de l'utilisateur.
     * @param reseau  Le {@link Reseau} sur lequel les opérations seront effectuées.
     */
    private static void menuCalcul(Scanner scanner, Reseau reseau) {
        while (true) {
            System.out.println("\n===== MENU CALCUL =====");
            System.out.println("1) Calculer le coût du réseau électrique actuel");
            System.out.println("2) Modifier une connexion");
            System.out.println("3) Afficher le réseau");
            System.out.println("4) Fin");
            System.out.print("Votre choix : ");

            int choix = askForInt(scanner);
            if (choix == -1) {
                continue; //Réaffiche le menu si l'entrée est invalide
            }

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
                            nouveauGen.getNom());
                }
                case 3 -> reseau.afficher();
                case 4 -> {
                    System.out.println("Fin du programme.");
                    return;
                }
                default -> System.out.println("Choix invalide !");
            }
        }
    }

    // ================================
    // MENU PARTIE 2 (mode fichier)
    // ================================
    /**
     * Affiche le menu pour le mode fichier (Partie 2) et gère les interactions.
     * Ce menu est spécifique au mode où un réseau est chargé depuis un fichier.
     * Il propose les options suivantes :
     * - Lancer une résolution automatique pour optimiser le réseau.
     * - Sauvegarder la configuration actuelle du réseau dans un fichier.
     * - Quitter le programme.
     *
     * @param scanner L'objet {@link Scanner} pour lire l'entrée de l'utilisateur.
     * @param reseau  Le {@link Reseau} chargé, sur lequel les opérations seront effectuées.
     */
    private static void menuPartie2(Scanner scanner, Reseau reseau) {
        while (true) {
            System.out.println("\n===== MENU (MODE FICHIER) =====");
            System.out.println("coût: " + reseau.calculerCout());
            System.out.println("1) Résolution automatique");
            System.out.println("2) Sauvegarder la solution actuelle");
            System.out.println("3) Fin");
            System.out.print("Votre choix : ");

            int choix = askForInt(scanner);
            if (choix == -1) {
                continue; // Réaffiche le menu si l'entrée est invalide
            }

            switch (choix) {
                case 1 -> {
                    System.out.println("\n=== Résolution automatique ===");
                    Reseau nouvelleSolution = Reseau.algoOptimise(reseau);
                    System.out.println("Coût de la solution trouvée : " + nouvelleSolution.calculerCout());
                    nouvelleSolution.afficher();
                }

                case 2 -> {
                    System.out.print("Entrez le nom du fichier de sauvegarde : ");
                    String nomFichier = scanner.nextLine().trim();

                    if (nomFichier.isEmpty()) {
                        System.out.println("Nom de fichier invalide.");
                        break;
                    }

                    try {
                        Reseau.sauvegarder(reseau, nomFichier);
                        System.out.println("Solution actuelle sauvegardée dans : " + nomFichier);
                    } catch (IOException e) {
                        System.out.println("Erreur lors de la sauvegarde : " + e.getMessage());
                    }
                }

                case 3 -> {
                    System.out.println("Fin du programme.");
                    return;
                }

                default -> System.out.println("Choix invalide ! Veuillez taper 1, 2 ou 3.");
            }
        }
    }

    /**
     * Point d'entrée principal de l'application.
     * Ce programme peut être exécuté de deux manières :
     * - Mode interactif : Si aucun argument n'est fourni, le programme
     * démarre un menu interactif pour construire un réseau électrique manuellement.
     * - Mode fichier : Si un chemin de fichier est fourni comme premier
     * argument, le programme charge la configuration du réseau depuis ce fichier.
     * Un deuxième argument optionnel (un entier) peut être utilisé pour spécifier
     * la valeur de lambda (sévérité).
     *
     * @param args Les arguments de la ligne de commande.
     *             - {@code args[0]} (optionnel) : Chemin vers le fichier de configuration du réseau.
     *             - {@code args[1]} (optionnel) : Valeur entière pour lambda (sévérité).
     */
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            Reseau reseau = new Reseau();

            // ============================
            // MODE FICHIER (PARTIE 2)
            // ============================
            if (args.length >= 1) {
                String path = args[0];

                int lambda = 10;
                if (args.length >= 2) {
                    try {
                        lambda = Integer.parseInt(args[1]);
                        reseau.setLambda(lambda);
                    } catch (NumberFormatException e) {
                        System.out.println("Valeur de λ invalide, la valeur 10 sera utilisée par défaut.");
                    }
                }

                try {
                    reseau.chargerReseauDepuisFichier(path);
                    System.out.println("Réseau chargé depuis le fichier : " + path);
                    reseau.afficher();
                    menuPartie2(scanner, reseau);
                } catch (IllegalArgumentException | IllegalStateException e) {
                    System.out.println("Erreur lors du chargement du fichier : " + e.getMessage());
                }
                return; // on ne passe pas au mode manuel
            }

            // ============================
            // MODE MANUEL (PARTIE 1)
            // ============================
            MaisonFactory maisonFactory = new MaisonFactory();
            GenerateurFactory generateurFactory = new GenerateurFactory(scanner);

            do {
                System.out.println("\n===== MENU PRINCIPAL =====");
                System.out.println("1) Ajouter un générateur");
                System.out.println("2) Ajouter une maison");
                System.out.println("3) ajouter une connexion entre une maison et un générateur existants");
                System.out.println("4) supprimer une connexion existante entre une maison et un générateur");
                System.out.println("5) Quitter et passer au menu calcul (si réseau valide)");
                System.out.print("Votre choix : ");

                int choix = askForInt(scanner);
                if (choix == -1) {
                    continue; // Réaffiche le menu si l'entrée est invalide
                }

                switch (choix) {
                    // --- Ajouter un générateur ---
                    case 1 -> {
                        try{
                            Generateur g = generateurFactory.creerGenerateur();
                            if (g != null)
                                reseau.ajouterGenerateur(g);
                        }catch(IllegalArgumentException e){
                            System.out.println(e.getMessage());
                        }

                    }

                    // --- Ajouter une maison ---
                    case 2 -> {
                        try{
                            Maison m = maisonFactory.creerMaison();
                            if (m != null)
                                reseau.ajouterMaison(m);
                        }catch (IllegalArgumentException e){
                            System.out.println(e.getMessage());
                        }

                    }

                    // --- Ajouter une connexion ---
                    case 3 -> {
                        if (reseau.isConnexionPossible()) {
                            System.out.println("Aucune connexion possible : vérifiez que vous avez au moins un générateur et une maison non connectée.");
                            break;
                        }

                        reseau.afficherOptions();

                        System.out.print("Entrez la maison et le générateur à connecter (ex: M1 G1 ou G1 M1): ");
                        String ligne = scanner.nextLine().trim();
                        String[] parties = ligne.split("\\s+");

                        if (parties.length != 2) {
                            System.out.println("Format invalide. Exemple attendu : M1 G1");
                            break;
                        }

                        String nom1 = parties[0];
                        String nom2 = parties[1];

                        Maison maison = reseau.getMaisonParNom(nom1);
                        Generateur generateur = reseau.getGenerateurParNom(nom2);

                        if (maison == null && generateur == null) {
                            maison = reseau.getMaisonParNom(nom2);
                            generateur = reseau.getGenerateurParNom(nom1);
                        }

                        if (maison == null || generateur == null) {
                            System.out.println("Maison ou générateur introuvable. Vérifiez que les deux existent.");
                            break;
                        }

                        reseau.ajouterConnexion(maison.getNom(), generateur.getNom());
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

                        Maison maison = reseau.getMaisonParNom(nom1);
                        Generateur generateur = reseau.getGenerateurParNom(nom2);

                        if (maison == null && generateur == null) {
                            maison = reseau.getMaisonParNom(nom2);
                            generateur = reseau.getGenerateurParNom(nom1);
                        }

                        if (maison == null || generateur == null) {
                            System.out.println("Maison ou générateur introuvable. Vérifiez que les deux existent.");
                            break;
                        }

                        reseau.supprimerConnexion(maison.getNom(), generateur.getNom());
                    }

                    // --- Vérifier que le réseau est valide, puis menu calcul ---
                    case 5 -> {
                        if (reseau.isValide()) {
                            menuCalcul(scanner, reseau);
                            return; // fin du programme après le menu calcul
                        } else {
                            System.out.println(
                                    "Votre réseau n'est pas valide, vérifiez que toutes les maisons sont connectées");
                        }
                    }

                    default -> System.out.println("Choix invalide !");
                }
            } while (true);
        }
    }
}