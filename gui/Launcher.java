package com.example.gui;

/**
 * Point d'entrée de l'application pour contourner les problèmes de lancement
 * de JavaFX sur certaines configurations.
 * Cette classe appelle simplement le `main` de {@link GuiMain}.
 * Elle peut être nécessaire pour créer des fichiers JAR exécutables.
 */
public class Launcher {
    /**
     * Lance l'application graphique.
     * @param args les arguments de la ligne de commande
     */
    public static void main(String[] args) {
        GuiMain.main(args);
    }
}
