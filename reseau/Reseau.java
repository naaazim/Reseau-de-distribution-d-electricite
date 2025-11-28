package com.example.reseau;

import java.util.List;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.LinkedHashMap;

/**
 * Gère l'ensemble du réseau électrique, y compris les générateurs, les maisons
 * et leurs connexions.
 * Cette classe centrale permet d'ajouter/modifier des composants, de calculer
 * les coûts,
 * et de charger/sauvegarder la configuration du réseau.
 * L'ordre d'insertion des générateurs est conservé.
 */
public class Reseau {
    private Map<Generateur, List<Maison>> connexions;
    private List<Maison> maisonsNonConnectees;
    private int capaciteTotale;
    private int lambda = 10;
    private int consommationTotale;

    /**
     * Construit un nouveau réseau électrique vide.
     */
    public Reseau() {
        connexions = new LinkedHashMap<>();
        maisonsNonConnectees = new ArrayList<>();
        capaciteTotale = 0;
        consommationTotale = 0;
    }

    /**
     * @return La carte des connexions actuelles, associant chaque générateur à sa
     *         liste de maisons.
     */
    public Map<Generateur, List<Maison>> getConnexions() {
        return connexions;
    }

    /**
     * @return La liste des maisons qui ne sont actuellement connectées à aucun
     *         générateur.
     */
    public List<Maison> getMaisonsNonConnectees() {
        return maisonsNonConnectees;
    }

    /**
     * @return Le facteur de pénalisation lambda utilisé dans le calcul du coût.
     */
    public int getLambda() {
        return lambda;
    }

    /**
     * Définit le facteur de pénalisation lambda.
     * 
     * @param lambda La nouvelle valeur pour lambda.
     */
    public void setLambda(int lambda) {
        this.lambda = lambda;
    }

    /**
     * Ajoute un nouveau générateur au réseau ou met à jour la capacité d'un
     * générateur existant.
     * La mise à jour est refusée si elle entraîne une capacité totale inférieure à
     * la consommation actuelle.
     *
     * @param g Le générateur à ajouter ou dont la capacité doit être mise à jour.
     */
    public void ajouterGenerateur(Generateur g) {
        for (Generateur existant : connexions.keySet()) {
            if (existant.getNom().equalsIgnoreCase(g.getNom())) {
                int ancienneCapacite = existant.getCapacite();
                int nouvelleCapacite = g.getCapacite();
                int nouvelleCapaciteTotale = capaciteTotale - ancienneCapacite + nouvelleCapacite;

                if (nouvelleCapaciteTotale < consommationTotale) {
                    throw new IllegalArgumentException("La capacité totale (" + nouvelleCapaciteTotale
                            + " kW) serait insuffisante pour la consommation actuelle (" + consommationTotale
                            + " kW).");
                }

                existant.setCapacite(nouvelleCapacite);
                capaciteTotale = nouvelleCapaciteTotale;
                return;
            }
        }

        connexions.put(g, new ArrayList<>());
        capaciteTotale += g.getCapacite();
    }

    /**
     * Ajoute une nouvelle maison au réseau (initialement non connectée) ou met à
     * jour la consommation d'une maison existante.
     * L'ajout ou la mise à jour est refusé si la consommation totale résultante
     * dépasse la capacité totale du réseau.
     *
     * @param m La maison à ajouter ou mettre à jour.
     */
    public void ajouterMaison(Maison m) {
        int nouvelleConso = m.getTypeConso().getConsommation();

        for (List<Maison> liste : connexions.values()) {
            for (Maison existante : liste) {
                if (existante.getNom().equalsIgnoreCase(m.getNom())) {
                    int ancienneConso = existante.getTypeConso().getConsommation();
                    int majTotale = consommationTotale - ancienneConso + nouvelleConso;

                    if (majTotale > capaciteTotale) {
                        throw new IllegalArgumentException("Capacité totale insuffisante (" + capaciteTotale
                                + " kW) pour cette mise à jour (" + majTotale + " kW).");
                    }

                    existante.setTypeConso(m.getTypeConso());
                    consommationTotale = majTotale;
                    return;
                }
            }
        }

        for (Maison existante : maisonsNonConnectees) {
            if (existante.getNom().equalsIgnoreCase(m.getNom())) {
                int ancienneConso = existante.getTypeConso().getConsommation();
                int majTotale = consommationTotale - ancienneConso + nouvelleConso;

                if (majTotale > capaciteTotale) {
                    throw new IllegalArgumentException("Capacité totale insuffisante (" + capaciteTotale
                            + " kW) pour cette mise à jour (" + majTotale + " kW).");
                }

                existante.setTypeConso(m.getTypeConso());
                consommationTotale = majTotale;
                return;
            }
        }

        int nouvelleTotale = consommationTotale + nouvelleConso;
        if (nouvelleTotale > capaciteTotale) {
            throw new IllegalArgumentException("Capacité totale insuffisante (" + capaciteTotale
                    + " kW) pour ajouter cette maison (" + nouvelleTotale + " kW).");
        }

        maisonsNonConnectees.add(m);
        consommationTotale = nouvelleTotale;
    }

    /**
     * Crée une connexion entre une maison et un générateur.
     * La maison doit exister et ne pas être déjà connectée à un autre générateur.
     *
     * @param nomMaison     Le nom de la maison à connecter.
     * @param nomGenerateur Le nom du générateur auquel se connecter.
     * @throws IllegalArgumentException si la maison est déjà connectée.
     */
    public void ajouterConnexion(String nomMaison, String nomGenerateur) {
        Generateur g = getGenerateurParNom(nomGenerateur);
        if (g == null) {
            throw new IllegalArgumentException("Le générateur '" + nomGenerateur + "' n'existe pas.");
        }

        Maison m = getMaisonParNom(nomMaison);
        if (m == null) {
            throw new IllegalArgumentException("La maison '" + nomMaison + "' n'existe pas.");
        }

        if (!maisonsNonConnectees.contains(m)) {
            throw new IllegalArgumentException("La maison '" + nomMaison + "' est déjà connectée.");
        }

        connexions.get(g).add(m);
        maisonsNonConnectees.remove(m);
    }

    /**
     * Supprime une connexion existante entre une maison et un générateur.
     * La maison devient alors non connectée.
     *
     * @param nomMaison     Le nom de la maison à déconnecter.
     * @param nomGenerateur Le nom du générateur dont la maison doit être
     *                      déconnectée.
     */
    public void supprimerConnexion(String nomMaison, String nomGenerateur) {
        Generateur g = getGenerateurParNom(nomGenerateur);
        if (g == null) {
            throw new IllegalArgumentException("Le générateur '" + nomGenerateur + "' est introuvable.");
        }
        Maison m = getMaisonParNom(nomMaison);
        if (m == null) {
            throw new IllegalArgumentException("La maison '" + nomMaison + "' est introuvable.");
        }

        if (connexions.containsKey(g) && connexions.get(g).contains(m)) {
            connexions.get(g).remove(m);
            maisonsNonConnectees.add(m);
        } else {
            throw new IllegalArgumentException(
                    "La maison " + nomMaison + " n'est pas connectée au générateur " + nomGenerateur + ".");
        }
    }

    /**
     * Vérifie s'il est possible d'ajouter de nouvelles connexions.
     * 
     * @return {@code true} s'il n'y a aucune maison non connectée ou aucun
     *         générateur, {@code false} sinon.
     */
    public boolean isConnexionPossible() {
        return maisonsNonConnectees.isEmpty() || connexions.isEmpty();
    }

    /**
     * Affiche les options de connexion disponibles, c'est-à-dire les maisons non
     * connectées,
     * puis l'état complet du réseau.
     */
    public void afficherOptions() {
        if (!maisonsNonConnectees.isEmpty()) {
            System.out.println("Maisons non connectées :");
            for (Maison m : maisonsNonConnectees) {
                System.out.print("  - ");
                m.afficher();
            }
        }
        afficher();
    }

    /**
     * Affiche l'état complet du réseau électrique, incluant chaque générateur et
     * les maisons qu'il alimente,
     * ainsi que la capacité et la consommation totales.
     */
    public void afficher() {
        System.out.println("\n===== RÉSEAU ÉLECTRIQUE =====");

        if (connexions.isEmpty()) {
            System.out.println("Aucun générateur dans le réseau.");
            return;
        }

        boolean premierGenerateur = true;
        for (Generateur g : connexions.keySet()) {
            if (!premierGenerateur) {
                System.out.println();
            }
            List<Maison> maisons = connexions.get(g);
            g.afficher();
            System.out.println(" alimente :");
            if (maisons.isEmpty()) {
                System.out.println("   Aucune maison connectée.");
            } else {
                for (Maison m : maisons) {
                    System.out.print("   - ");
                    m.afficher();
                }
            }
            premierGenerateur = false;
        }
        System.out.println(
                "Capacité totale : " + capaciteTotale + " kW | Consommation totale : " + consommationTotale + " kW");
    }

    /**
     * Construit et retourne une représentation textuelle de l'état actuel du
     * réseau.
     * 
     * @return Une chaîne de caractères décrivant le réseau.
     */
    public String getNetworkState() {
        StringBuilder sb = new StringBuilder();
        sb.append("===== RÉSEAU ÉLECTRIQUE =====\n");

        if (connexions.isEmpty()) {
            sb.append("Aucun générateur dans le réseau.\n");
        } else {
            boolean premierGenerateur = true;
            for (Map.Entry<Generateur, List<Maison>> entry : connexions.entrySet()) {
                if (!premierGenerateur) {
                    sb.append("\n");
                }
                Generateur g = entry.getKey();
                List<Maison> maisons = entry.getValue();

                sb.append(g.toString()).append(" alimente :\n");
                if (maisons.isEmpty()) {
                    sb.append("   Aucune maison connectée.\n");
                } else {
                    for (Maison m : maisons) {
                        sb.append("   - ").append(m.toString()).append("\n");
                    }
                }
                premierGenerateur = false;
            }
        }

        sb.append("\nCapacité totale : ").append(capaciteTotale).append(" kW | Consommation totale : ")
                .append(consommationTotale).append(" kW\n");

        if (!maisonsNonConnectees.isEmpty()) {
            sb.append("\nMaisons non connectées :\n");
            for (Maison m : maisonsNonConnectees) {
                sb.append("  - ").append(m.toString()).append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * Recherche et retourne un générateur par son nom.
     *
     * @param nom Le nom du générateur à rechercher (insensible à la casse).
     * @return L'objet {@link Generateur} correspondant, ou {@code null} s'il n'est
     *         pas trouvé.
     */
    public Generateur getGenerateurParNom(String nom) {
        for (Generateur g : connexions.keySet()) {
            if (g.getNom().equalsIgnoreCase(nom))
                return g;
        }
        return null;
    }

    /**
     * Recherche et retourne une maison par son nom, qu'elle soit connectée ou non.
     *
     * @param nom Le nom de la maison à rechercher (insensible à la casse).
     * @return L'objet {@link Maison} correspondant, ou {@code null} si elle n'est
     *         pas trouvée.
     */
    public Maison getMaisonParNom(String nom) {

        for (List<Maison> liste : connexions.values()) {
            for (Maison m : liste) {
                if (m.getNom().equalsIgnoreCase(nom))
                    return m;
            }
        }
        for (Maison m : maisonsNonConnectees) {
            if (m.getNom().equalsIgnoreCase(nom))
                return m;
        }
        return null;
    }

    /**
     * Vérifie si le réseau est dans un état valide.
     * Un réseau est considéré comme valide si toutes les maisons sont connectées à
     * un générateur.
     *
     * @return {@code true} si toutes les maisons sont connectées, {@code false}
     *         sinon.
     */
    public boolean isValide() {
        return maisonsNonConnectees.isEmpty();
    }

    /**
     * Calcule le taux d'utilisation d'un générateur donné.
     * Le taux est le rapport entre la charge consommée par les maisons connectées
     * et la capacité du générateur.
     *
     * @param g Le générateur pour lequel calculer le taux.
     * @return Le taux d'utilisation (entre 0.0 et 1.0+), ou 0 si le générateur
     *         n'est pas trouvé ou a une capacité nulle.
     */
    public double getTauxUtilisation(Generateur g) {
        if (!connexions.containsKey(g)) {
            System.out.println(" Générateur " + g.getNom() + " non trouvé dans le réseau.");
            return 0;
        }

        int charge = 0;
        for (Maison m : connexions.get(g)) {
            charge += m.getTypeConso().getConsommation();
        }

        if (g.getCapacite() == 0)
            return 0;
        return (double) charge / g.getCapacite();
    }

    /**
     * Calcule la dispersion des taux d'utilisation entre tous les générateurs du
     * réseau.
     *
     * @return La valeur de dispersion.
     */
    public double dispersion() {
        if (connexions.isEmpty())
            return 0;

        Map<Generateur, Double> taux = new HashMap<>();
        double somme = 0;

        for (Generateur g : connexions.keySet()) {
            double u = getTauxUtilisation(g);
            taux.put(g, u);
            somme += u;
        }

        double moyenne = somme / connexions.size();

        double dispersion = 0;
        for (double u : taux.values()) {
            dispersion += Math.abs(u - moyenne);
        }

        return dispersion;
    }

    /**
     * Calcule la surcharge totale du réseau.
     * La surcharge est la somme des surcharges relatives de chaque générateur.
     *
     * @return La valeur de la surcharge.
     */
    public double surcharge() {
        if (connexions.isEmpty())
            return 0;
        double surcharge = 0;
        for (Generateur g : connexions.keySet()) {
            double chargeActuelle = 0;
            for (Maison m : connexions.get(g)) {
                chargeActuelle += m.getTypeConso().getConsommation();
            }
            surcharge += Math.max(0, (double) (chargeActuelle - g.getCapacite()) / g.getCapacite());
        }
        return surcharge;
    }

    /**
     * Calcule le coût total du réseau, basé sur la dispersion et la surcharge.
     * Le coût est calculé comme : {@code dispersion + lambda * surcharge}.
     *
     * @return Le coût total du réseau.
     */
    public double calculerCout() {
        return dispersion() + lambda * surcharge();
    }

    /**
     * Modifie une connexion existante en déplaçant une maison d'un générateur à un
     * autre.
     *
     * @param ancienneMaison    Le nom de la maison à déplacer.
     * @param ancienGenerateur  Le nom du générateur d'origine.
     * @param nouvelleMaison    Doit être le même que {@code ancienneMaison}.
     * @param nouveauGenerateur Le nom du générateur de destination.
     */
    public void modifierConnexion(String ancienneMaison, String ancienGenerateur, String nouvelleMaison,
            String nouveauGenerateur) {
        Maison maison = getMaisonParNom(ancienneMaison);
        Generateur ancienGen = getGenerateurParNom(ancienGenerateur);
        Generateur nouveauGen = getGenerateurParNom(nouveauGenerateur);

        if (maison == null) {
            System.out.println("Erreur de modification : la maison '" + ancienneMaison + "' est introuvable.");
            return;
        }
        if (ancienGen == null) {
            System.out.println(
                    "Erreur de modification : l'ancien générateur '" + ancienGenerateur + "' est introuvable.");
            return;
        }
        if (nouveauGen == null) {
            System.out.println(
                    "Erreur de modification : le nouveau générateur '" + nouveauGenerateur + "' est introuvable.");
            return;
        }

        if (!connexions.containsKey(ancienGen) || !connexions.get(ancienGen).contains(maison)) {
            System.out.println("La maison " + maison.getNom() + " n'est pas connectée à " + ancienGen.getNom() + ".");
            return;
        }

        if (!connexions.containsKey(nouveauGen)) {
            System.out.println("Le générateur " + nouveauGenerateur + " n'existe pas dans le réseau.");
            return;
        }

        if (connexions.get(nouveauGen).contains(maison)) {
            System.out.println("La maison " + maison.getNom() + " est déjà connectée à " + nouveauGen.getNom() + ".");
            return;
        }

        connexions.get(ancienGen).remove(maison);
        connexions.get(nouveauGen).add(maison);

        System.out.println("Connexion modifiée : " + maison.getNom() +
                " passe de " + ancienGen.getNom() + " à " + nouveauGen.getNom() + ".");
    }

    /**
     * Extrait le nom de la maison à partir d'un tableau de chaînes.
     *
     * @param parts Le tableau de chaînes (généralement issu d'une saisie
     *              utilisateur).
     * @return La première {@link Maison} trouvée correspondant à un nom dans le
     *         tableau, ou {@code null}.
     */
    public Maison getMaisonDepuisLigne(String[] parts) {
        for (String s : parts) {
            Maison m = getMaisonParNom(s);
            if (m != null)
                return m;
        }
        return null;
    }

    /**
     * Extrait le nom du générateur à partir d'un tableau de chaînes.
     *
     * @param parts Le tableau de chaînes (généralement issu d'une saisie
     *              utilisateur).
     * @return Le premier {@link Generateur} trouvé correspondant à un nom dans le
     *         tableau, ou {@code null}.
     */
    public Generateur getGenerateurDepuisLigne(String[] parts) {
        for (String s : parts) {
            Generateur g = getGenerateurParNom(s);
            if (g != null)
                return g;
        }
        return null;
    }

    /**
     * Charge une configuration de réseau à partir d'un fichier texte.
     * Le fichier doit respecter un format spécifique pour les générateurs, les
     * maisons et les connexions.
     *
     * @param path Le chemin vers le fichier de configuration.
     * @throws IllegalArgumentException si le fichier contient des erreurs de
     *                                  syntaxe ou de logique.
     * @throws IllegalStateException    si, à la fin du chargement, le réseau n'est
     *                                  pas valide (maisons non connectées).
     */
    public void chargerReseauDepuisFichier(String path) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {

            String ligne;
            int etape = 0;
            int numeroLigne = 0;

            while ((ligne = br.readLine()) != null) {
                numeroLigne++;
                ligne = ligne.trim();

                if (ligne.isEmpty())
                    continue;

                if (!ligne.endsWith(".")) {
                    throw new IllegalArgumentException(
                            "ligne " + numeroLigne + ") : ligne sans point final -> " + ligne);
                }

                ligne = ligne.substring(0, ligne.length() - 1);

                if (ligne.toLowerCase().startsWith("generateur(")) {
                    if (etape > 0)
                        throw new IllegalArgumentException("ligne " + numeroLigne +
                                ") : générateur après les maisons -> " + ligne);

                    etape = 0;

                    try {
                        parseGenerateur(ligne);
                    } catch (Exception e) {
                        throw new IllegalArgumentException(
                                "ligne " + numeroLigne + ") : " + e.getMessage());
                    }
                    continue;
                }

                if (ligne.toLowerCase().startsWith("maison(")) {
                    if (etape > 1)
                        throw new IllegalArgumentException("ligne " + numeroLigne +
                                ") : maison après les connexions -> " + ligne);

                    etape = 1;

                    try {
                        parseMaison(ligne);
                    } catch (Exception e) {
                        throw new IllegalArgumentException(
                                "ligne " + numeroLigne + ") : " + e.getMessage());
                    }
                    continue;
                }

                if (ligne.toLowerCase().startsWith("connexion(")) {
                    etape = 2;

                    try {
                        parseConnexion(ligne);
                    } catch (Exception e) {
                        throw new IllegalArgumentException(
                                "ligne " + numeroLigne + ") : " + e.getMessage());
                    }
                    continue;
                }

                throw new IllegalArgumentException(
                        "ligne " + numeroLigne + ") : syntaxe inconnue -> " + ligne);
            }

            if (!isValide()) {
                throw new IllegalStateException(" Certaines maisons ne sont pas connectées )");
            }

        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("Fichier non trouvé : " + path);
        } catch (IOException e) {
            throw new RuntimeException("Erreur d'entrée/sortie lors de la lecture du fichier : " + e.getMessage(), e);
        }
    }

    /**
     * Analyse une chaîne de caractères pour en extraire les informations d'un générateur
     * et l'ajouter au réseau.
     * Le format attendu est "generateur(nom,capacité)".
     *
     * @param ligne La chaîne à analyser.
     * @throws IllegalArgumentException si le format est invalide ou si la capacité n'est pas un nombre.
     */
    private void parseGenerateur(String ligne) {
        ligne = ligne.substring("generateur(".length(), ligne.length() - 1);
        String[] parts = ligne.split(",");

        if (parts.length != 2) {
            throw new IllegalArgumentException(
                    "Format générateur invalide, attendu : generateur(nom,capacité) -> " + ligne);
        }

        String nom = parts[0].trim();
        int capacite;
        try {
            capacite = Integer.parseInt(parts[1].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "La capacité du générateur doit être un nombre entier. Valeur reçue : '" + parts[1].trim() + "'");
        }

        ajouterGenerateur(new Generateur(nom, capacite));
    }

    /**
     * Analyse une chaîne de caractères pour en extraire les informations d'une maison
     * et l'ajouter au réseau.
     * Le format attendu est "maison(nom,TYPE_CONSO)".
     *
     * @param ligne La chaîne à analyser.
     * @throws IllegalArgumentException si le format est invalide ou si le type de consommation est inconnu.
     */
    private void parseMaison(String ligne) {
        ligne = ligne.substring("maison(".length(), ligne.length() - 1);
        String[] parts = ligne.split(",");

        if (parts.length != 2) {
            throw new IllegalArgumentException("Format maison invalide : " + ligne);
        }

        String nom = parts[0].trim();
        String type = parts[1].trim().toUpperCase();

        TypeConso conso;

        try {
            conso = TypeConso.valueOf(type);
        } catch (Exception e) {
            throw new IllegalArgumentException("Type de consommation inconnu : " + type);
        }

        ajouterMaison(new Maison(nom, conso));
    }

    /**
     * Analyse une chaîne de caractères pour créer une connexion entre une maison et un générateur.
     * Le format attendu est "connexion(nomMaison,nomGenerateur)".
     *
     * @param ligne La chaîne à analyser.
     * @throws IllegalArgumentException si le format est invalide ou si les entités n'existent pas.
     */
    private void parseConnexion(String ligne) {
        ligne = ligne.substring("connexion(".length(), ligne.length() - 1);
        String[] parts = ligne.split(",");

        if (parts.length != 2) {
            throw new IllegalArgumentException("Format connexion invalide : " + ligne);
        }

        String a = parts[0].trim();
        String b = parts[1].trim();

        Maison m = getMaisonParNom(a);
        Generateur g = getGenerateurParNom(b);

        if (m != null && g != null) {
            ajouterConnexion(m.getNom(), g.getNom());
            return;
        }

        m = getMaisonParNom(b);
        g = getGenerateurParNom(a);

        if (m != null && g != null) {
            ajouterConnexion(m.getNom(), g.getNom());
            return;
        }

        throw new IllegalArgumentException("Connexion impossible : " + a + " - " + b);
    }

    /**
     * @return Un générateur aléatoire du réseau, ou {@code null} s'il n'y en a pas.
     */
    public Generateur getGenerateurAleatoire() {
        List<Generateur> liste = new ArrayList<>(connexions.keySet());
        if (liste.isEmpty())
            return null;
        return liste.get(new Random().nextInt(liste.size()));
    }

    /**
     * @return Une maison aléatoire du réseau (connectée ou non), ou {@code null}
     *         s'il n'y en a pas.
     */
    public Maison getMaisonAleatoire() {
        List<Maison> toutesLesMaison = new ArrayList<>();
        for (List<Maison> l : connexions.values()) {
            toutesLesMaison.addAll(l);
        }
        toutesLesMaison.addAll(maisonsNonConnectees);

        if (toutesLesMaison.isEmpty())
            return null;
        return toutesLesMaison.get(new Random().nextInt(toutesLesMaison.size()));
    }

    /**
     * Exécute un algorithme d'optimisation simple (recherche locale naïve)
     * pendant un nombre fixe d'itérations.
     * À chaque itération, une maison est déplacée aléatoirement vers un autre
     * générateur. Si le coût du réseau augmente, le changement est annulé.
     *
     * @param reseau Le réseau initial à optimiser.
     * @param k      Le nombre d'itérations à effectuer.
     * @return Le réseau potentiellement optimisé après k itérations.
     */
    public Reseau algoNaif(Reseau reseau, int k) {
        int i = 0;

        while (i < k) {
            Maison m = reseau.getMaisonAleatoire();
            Generateur g = reseau.getGenerateurAleatoire();

            if (m == null || g == null)
                break;

            double ancienCout = reseau.calculerCout();

            String ancienGen = "";
            for (Generateur gen : reseau.getConnexions().keySet()) {
                if (reseau.getConnexions().get(gen).contains(m)) {
                    ancienGen = gen.getNom();
                    break;
                }
            }

            reseau.modifierConnexion(m.getNom(), ancienGen, m.getNom(), g.getNom());
            double nouveauCout = reseau.calculerCout();

            if (nouveauCout > ancienCout) {
                reseau.modifierConnexion(m.getNom(), g.getNom(), m.getNom(), ancienGen);
            }
            i++;
        }
        return reseau;
    }

    /**
     * Exécute un algorithme d'optimisation avancé basé sur une recherche locale
     * de type "meilleure amélioration" (best improvement).
     * L'algorithme parcourt toutes les maisons et évalue le coût de leur
     * déplacement vers chaque autre générateur. La maison est déplacée vers le
     * générateur qui offre la plus grande réduction de coût.
     * Le processus est répété jusqu'à ce qu'un passage complet sur toutes les
     * maisons ne produise plus aucune amélioration, garantissant ainsi un minimum local.
     *
     * @param reseau Le réseau de départ à optimiser.
     * @return Le réseau optimisé, potentiellement dans un état de coût minimal local.
     */
    public static Reseau algoOptimise(Reseau reseau) {

        boolean ameliorationTrouvee = true;

        while (ameliorationTrouvee) {
            ameliorationTrouvee = false;

            List<Maison> toutesLesMaison = new ArrayList<>();
            for (List<Maison> l : reseau.getConnexions().values()) {
                toutesLesMaison.addAll(l);
            }

            for (Maison maison : toutesLesMaison) {

                Generateur ancienGenerateur = null;
                for (Generateur generateur : reseau.getConnexions().keySet()) {
                    if (reseau.getConnexions().get(generateur).contains(maison)) {
                        ancienGenerateur = generateur;
                        break;
                    }
                }

                if (ancienGenerateur == null) {
                    continue;
                }

                for (Generateur generateur : reseau.getConnexions().keySet()) {
                    if (!generateur.equals(ancienGenerateur)) {

                        double ancienCout = reseau.calculerCout();

                        reseau.modifierConnexion(maison.getNom(),
                                ancienGenerateur.getNom(),
                                maison.getNom(),
                                generateur.getNom());

                        double nouveauCout = reseau.calculerCout();
                        if (nouveauCout > ancienCout) {
                            reseau.modifierConnexion(maison.getNom(),
                                    generateur.getNom(),
                                    maison.getNom(),
                                    ancienGenerateur.getNom());
                        } else {
                            ancienGenerateur = generateur;
                            ameliorationTrouvee = true;
                        }
                    }
                }
            }
        }

        return reseau;
    }

    /**
     * Sauvegarde l'état actuel du réseau dans un fichier texte.
     *
     * @param reseau Le réseau à sauvegarder.
     * @param path   Le chemin du fichier de destination.
     * @throws IOException En cas d'erreur lors de l'écriture du fichier.
     */
    public static void sauvegarder(Reseau reseau, String path) throws IOException {
        try (FileWriter fw = new FileWriter(path)) {

            for (Generateur g : reseau.getConnexions().keySet()) {
                fw.write("generateur(" + g.getNom() + "," + g.getCapacite() + ").\n");
            }

            Set<String> dejaEcrites = new HashSet<>();

            for (List<Maison> liste : reseau.getConnexions().values()) {
                for (Maison m : liste) {
                    if (dejaEcrites.add(m.getNom())) {
                        fw.write("maison(" + m.getNom() + "," + m.getTypeConso().name() + ").\n");
                    }
                }
            }

            for (Maison m : reseau.getMaisonsNonConnectees()) {
                if (dejaEcrites.add(m.getNom())) {
                    fw.write("maison(" + m.getNom() + "," + m.getTypeConso().name() + ").\n");
                }
            }

            for (Generateur g : reseau.getConnexions().keySet()) {
                for (Maison m : reseau.getConnexions().get(g)) {
                    fw.write("connexion(" + g.getNom() + "," + m.getNom() + ").\n");
                }
            }
        }
    }
}
