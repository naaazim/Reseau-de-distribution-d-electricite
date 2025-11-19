package main.java.com.example.reseau;
import java.util.Scanner;

//Nous allons utiliser le Design Pattern Factory pour créer de nouveaux objets de type Generateur grâce à une saisie utilisteur 
public class GenerateurFactory {
    private Scanner scanner;
    
    public GenerateurFactory(Scanner scanner){
        this.scanner = scanner;
    }

    public Generateur creerGenerateur(){
        System.out.println("Entrez le nom du générateur suivi de sa capacité en kW (ex: G1 60): ");
        String ligne = scanner.nextLine().trim();
        String parties[] = ligne.split("\\s+");
        if (parties.length != 2) {
            System.out.println("Format invalide. Exemple de saisie attendu : G1 60");
            return null;
        }
        String nom = parties[0];
        try{
            int capacite = Integer.parseInt(parties[1]);
            return new Generateur(nom.toUpperCase(), capacite);
        }catch(IllegalArgumentException e){
            System.err.println(e.getMessage());
            return null;
        }
    }
}
