package com.example.factory;

import com.example.reseau.Maison;
import com.example.reseau.TypeConso;

import java.util.Scanner;

/**
 * Implémente le Design Pattern Factory pour créer des objets {@link Maison}.
 * Cette factory utilise les entrées utilisateur via la console pour construire les objets.
 */
public class MaisonFactory {
    private Scanner scanner;

    /**
     * Construit une nouvelle factory de maisons.
     */
    public MaisonFactory() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Crée un nouvel objet {@link Maison} à partir des entrées de l'utilisateur.
     * La méthode demande un nom et un type de consommation, valide l'entrée, et retourne le nouvel objet.
     *
     * @return Un nouvel objet {@code Maison} si l'entrée est valide, sinon {@code null}.
     */
    public Maison creerMaison() {
        System.out.print("Entrez le nom et le type de consommation (BASSE - NORMAL - FORTE) (ex: M1 NORMAL) : ");
        String ligne = scanner.nextLine().trim();
        String[] parties = ligne.split("\\s+");

        if (parties.length != 2) {
            System.out.println("Format invalide. Exemple attendu : M1 NORMAL");
            return null;
        }

        String nom = parties[0];
        String typeStr = parties[1].toUpperCase();

        try {
            TypeConso type = TypeConso.valueOf(typeStr);
            return new Maison(nom.toUpperCase(), type);
        } catch (IllegalArgumentException e) {
            System.out.println("Type de consommation invalide. Utilisez BASSE, NORMAL ou FORTE.");
            return null;
        }
    }
}
