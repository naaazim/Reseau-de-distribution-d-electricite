package com.example.factory;

import com.example.reseau.Maison;
import com.example.reseau.TypeConso;

import java.util.Scanner;

// Nous allons utiliser le Desing Pattern Factory pour créer des objets Maison à partir d'une saisie utilisateur sur une seule ligne
public class MaisonFactory {
    private Scanner scanner;

    public MaisonFactory() {
        this.scanner = new Scanner(System.in);
    }

    public Maison creerMaison() {
        System.out.print("Entrez le nom et le type de consommation (BASSE - NORMAL - FORTE) (ex: M1 NORMAL) : ");
        String ligne = scanner.nextLine().trim();
        String[] parties = ligne.split("\\s+");

        if (parties.length != 2) {
            System.err.println("Format invalide. Exemple attendu : M1 NORMAL");
            return null;
        }

        String nom = parties[0];
        String typeStr = parties[1].toUpperCase();

        try {
            TypeConso type = TypeConso.valueOf(typeStr);
            return new Maison(nom.toUpperCase(), type);
        } catch (IllegalArgumentException e) {
            System.err.println("Type de consommation invalide. Utilisez BASSE, NORMAL ou FORTE.");
            return null;
        }
    }
}
