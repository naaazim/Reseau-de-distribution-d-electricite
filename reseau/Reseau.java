package main.java.com.example.reseau;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Reseau {
    private Map<Generateur, List<Maison>> connexions; // Générateur -> liste de maisons
    private List<Maison> maisonsNonConnectees;        // Maisons créées mais pas encore reliées
    private int capaciteTotale;
    private int lambda = 10;
    private int consommationTotale;

    public Reseau() {
        //Utilisation de linkedHashMap pour conserver l'ordre d'ajout des éléments
        connexions = new LinkedHashMap<>();
        maisonsNonConnectees = new ArrayList<>();
        capaciteTotale = 0;
        consommationTotale = 0;
    }

    public Map<Generateur, List<Maison>> getConnexions() {
        return connexions;
    }

    public List<Maison> getMaisonsNonConnectees() {
        return maisonsNonConnectees;
    }

    public int getLambda() { return lambda; }

    public void setLambda(int lambda) {
        this.lambda = lambda;
    }

    // --- Ajouter ou mettre à jour un générateur ---
    public void ajouterGenerateur(Generateur g) {
        for (Generateur existant : connexions.keySet()) {
            if (existant.getNom().equalsIgnoreCase(g.getNom())) {
                System.out.println("Le générateur " + g.getNom() + " existe déjà.");

                int ancienneCapacite = existant.getCapacite();
                int nouvelleCapacite = g.getCapacite();
                int nouvelleCapaciteTotale = capaciteTotale - ancienneCapacite + nouvelleCapacite;

                if (nouvelleCapaciteTotale < consommationTotale) {
                    System.err.println("Mise à jour impossible : la capacité totale (" + nouvelleCapaciteTotale +
                            " kW) serait inférieure à la consommation actuelle (" + consommationTotale + " kW).");
                    return;
                }

                existant.setCapacite(nouvelleCapacite);
                capaciteTotale = nouvelleCapaciteTotale;
                System.out.println("Capacité du générateur " + existant.getNom() +
                        " mise à jour à " + nouvelleCapacite + " kW.");
                System.out.println("Capacité totale du réseau : " + capaciteTotale + " kW.");
                return;
            }
        }

        // Ajout d’un nouveau générateur
        connexions.put(g, new ArrayList<>());
        capaciteTotale += g.getCapacite();
        System.out.println("Générateur ajouté : " + g + " | Capacité totale du réseau : " + capaciteTotale + " kW");
    }

    // --- Ajouter ou mettre à jour une maison (non connectée) ---
    public void ajouterMaison(Maison m) {
        int nouvelleConso = m.getTypeConso().getConsommation();

        // --- Vérifie si la maison existe déjà (connectée ou non) ---
        for (List<Maison> liste : connexions.values()) {
            for (Maison existante : liste) {
                if (existante.getNom().equalsIgnoreCase(m.getNom())) {
                    int ancienneConso = existante.getTypeConso().getConsommation();
                    int majTotale = consommationTotale - ancienneConso + nouvelleConso;

                    if (majTotale > capaciteTotale) {
                        System.err.println("Mise à jour impossible : la consommation totale (" + majTotale +
                                " kW) dépasserait la capacité totale du réseau (" + capaciteTotale + " kW).");
                        return;
                    }

                    existante.setTypeConso(m.getTypeConso());
                    consommationTotale = majTotale;
                    System.out.println("Mise à jour de la consommation de " + m.getNom() +
                            ". Nouvelle consommation totale : " + consommationTotale + " kW.");
                    return;
                }
            }
        }

        for (Maison existante : maisonsNonConnectees) {
            if (existante.getNom().equalsIgnoreCase(m.getNom())) {
                int ancienneConso = existante.getTypeConso().getConsommation();
                int majTotale = consommationTotale - ancienneConso + nouvelleConso;

                if (majTotale > capaciteTotale) {
                    System.err.println("Mise à jour impossible : la consommation totale (" + majTotale +
                            " kW) dépasserait la capacité totale du réseau (" + capaciteTotale + " kW).");
                    return;
                }

                existante.setTypeConso(m.getTypeConso());
                consommationTotale = majTotale;
                System.out.println("Type de consommation mis à jour pour " + m.getNom() +
                        ". Nouvelle consommation totale : " + consommationTotale + " kW.");
                return;
            }
        }

        // --- Si c’est une nouvelle maison ---
        int nouvelleTotale = consommationTotale + nouvelleConso;
        if (nouvelleTotale > capaciteTotale) {
            System.err.println("Ajout impossible : la consommation totale (" + nouvelleTotale +
                    " kW) dépasserait la capacité totale du réseau (" + capaciteTotale + " kW).");
            return;
        }

        // --- Ajout validé ---
        maisonsNonConnectees.add(m);
        consommationTotale = nouvelleTotale;
        System.out.println("Maison ajoutée : " + m + " (non connectée). Consommation totale : "
                + consommationTotale + " kW.");
    }


    // --- Ajouter une connexion (relier une maison à un générateur) ---
    public void ajouterConnexion(String nomMaison, String nomGenerateur) {
        Generateur g = getGenerateurParNom(nomGenerateur);
        if (g == null) {
            System.err.println("Le générateur " + nomGenerateur + " n'existe pas.");
            return;
        }

        Maison m = getMaisonParNom(nomMaison);
        if (m == null) {
            System.err.println("La maison " + nomMaison + " n'existe pas. Veuillez d'abord l'ajouter.");
            return;
        }

        // Vérifie si  déjà connectée
        for (List<Maison> liste : connexions.values()) {
            if (liste.contains(m)) {
                throw new IllegalArgumentException("La maison " + nomMaison + " est déjà connectée à un générateur.");
            }
        }

        connexions.get(g).add(m);
        maisonsNonConnectees.remove(m);
        System.out.println("Connexion ajoutée : " + g.getNom() + " -> " + m.getNom() +
                " | Consommation totale : " + consommationTotale + " kW");
    }
    public void supprimerConnexion(String nomMaison, String nomGenerateur){
        Generateur g = getGenerateurParNom(nomGenerateur);
        Maison m = getMaisonParNom(nomMaison);
        if(g != null){
            if(connexions.get(g).contains(m)){
                connexions.get(g).remove(m);
                maisonsNonConnectees.add(m);
                System.out.println("Connexion supprimée avec succés");
            }
        }else{
            System.out.println("La maison " + nomMaison + " n'est pas connectée au générateur " + nomGenerateur);
        }
    }
    public boolean isConnexionPossible(){
        return maisonsNonConnectees.isEmpty() || connexions.isEmpty();
    }
    //Cette méthode sera utilisée au moment de l'ajout d'une connexion et permettera d'afficher les options de connexions disponibles (Generateurs + maisons non connectées)
    public void afficherOptions(){
        for(Maison m : maisonsNonConnectees){
            System.out.println("Maisons non connectées");
            System.out.print("  - ");
            m.afficher();
        }
        afficher();
    }
    // --- Afficher le réseau complet ---
    public void afficher() {
        System.out.println("\n===== RÉSEAU ÉLECTRIQUE =====");

        if (connexions.isEmpty()) {
            System.out.println("Aucun générateur dans le réseau.");
            return;
        }

        for (Generateur g : connexions.keySet()) {
            List<Maison> maisons = connexions.get(g);
            System.out.println("");
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
        }
        System.out.println("\nCapacité totale : " + capaciteTotale + " kW | Consommation totale : " + consommationTotale + " kW");
    }

    public Generateur getGenerateurParNom(String nom) {
        for (Generateur g : connexions.keySet()) {
            if (g.getNom().equalsIgnoreCase(nom)) return g;
        }
        return null;
    }

    public Maison getMaisonParNom(String nom) {
        
        for (List<Maison> liste : connexions.values()) {
            for (Maison m : liste) {
                if (m.getNom().equalsIgnoreCase(nom)) return m;
            }
        }
        for (Maison m : maisonsNonConnectees) {
            if (m.getNom().equalsIgnoreCase(nom)) return m;
        }
        return null;
    }
    public boolean isValide(){
        return maisonsNonConnectees.isEmpty();
    }
    public double getTauxUtilisation(Generateur g) {
        if (!connexions.containsKey(g)) {
            System.out.println(" Générateur " + g.getNom() + " non trouvé dans le réseau.");
            return 0;
        }

        int charge = 0;
        for (Maison m : connexions.get(g)) {
            charge += m.getTypeConso().getConsommation();
        }

        if (g.getCapacite() == 0) return 0; // éviter division par zéro
        return (double) charge / g.getCapacite();
    }
    public double dispersion() {
        if (connexions.isEmpty()) return 0;

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
    public double surcharge(){
        if(connexions.isEmpty())return 0;
        double surcharge = 0;
        for(Generateur g : connexions.keySet()){
            double chargeActuelle = 0;
            for(Maison m : connexions.get(g)){
                chargeActuelle += m.getTypeConso().getConsommation();
            }
            surcharge += Math.max(0, (double)(chargeActuelle - g.getCapacite()) / g.getCapacite());
        }
        return surcharge;
    }

    public double calculerCout(){
        // Sévérité de la pénalisation
        return dispersion() + lambda * surcharge();
    }
    public void modifierConnexion(String ancienneMaison, String ancienGenerateur,String nouvelleMaison, String nouveauGenerateur) {
        Maison maison = getMaisonParNom(ancienneMaison);
        Generateur ancienGen = getGenerateurParNom(ancienGenerateur);
        Generateur nouveauGen = getGenerateurParNom(nouveauGenerateur);

        // Vérifier que la maison est bien connectée à l’ancien générateur
        if (!connexions.containsKey(ancienGen) || !connexions.get(ancienGen).contains(maison)) {
            System.out.println("La maison " + maison.getNom() + " n'est pas connectée à " + ancienGen.getNom() + ".");
            return;
        }

        // Vérifier que le nouveau générateur existe dans le réseau
        if (!connexions.containsKey(nouveauGen)) {
            System.out.println("Le générateur " + nouveauGenerateur + " n'existe pas dans le réseau.");
            return;
        }

        // Vérifier que la maison n’est pas déjà connectée à ce générateur
        if (connexions.get(nouveauGen).contains(maison)) {
            System.out.println("La maison " + maison.getNom() + " est déjà connectée à " + nouveauGen.getNom() + ".");
            return;
        }

        // --- Modification effective ---
        connexions.get(ancienGen).remove(maison);
        connexions.get(nouveauGen).add(maison);

        System.out.println("Connexion modifiée : " + maison.getNom() +
                        " passe de " + ancienGen.getNom() + " à " + nouveauGen.getNom() + ".");
    }

    public Maison getMaisonDepuisLigne(String[] parts) {
        for (String s : parts) {
            Maison m = getMaisonParNom(s);
            if (m != null) return m;
        }
        return null;
    }

    public Generateur getGenerateurDepuisLigne(String[] parts) {
        for (String s : parts) {
            Generateur g = getGenerateurParNom(s);
            if (g != null) return g;
        }
        return null;
    }

    // --- Méthode pour charger un réseau depuis un fichier 
    public void chargerReseauDepuisFichier(String path) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {

            String ligne;
            int etape = 0; // 0=générateurs, 1=maisons, 2=connexions
            int numeroLigne = 0;

            while ((ligne = br.readLine()) != null) {
                numeroLigne++;
                ligne = ligne.trim();

                if (ligne.isEmpty()) continue;

                if (!ligne.endsWith(".")) {
                    throw new IllegalArgumentException(
                            "ligne " + numeroLigne + ") : ligne sans point final -> " + ligne
                    );
                }

                ligne = ligne.substring(0, ligne.length() - 1);

                // -------------------------
                // GENERATEUR
                // -------------------------
                if (ligne.toLowerCase().startsWith("generateur(")) {
                    if (etape > 0)
                        throw new IllegalArgumentException("ligne " + numeroLigne +
                                ") : générateur après les maisons -> " + ligne);

                    etape = 0;

                    try {
                        parseGenerateur(ligne);
                    } catch (Exception e) {
                        throw new IllegalArgumentException(
                                "ligne " + numeroLigne + ") : " + e.getMessage()
                        );
                    }
                    continue;
                }

                // -------------------------
                // MAISON
                // -------------------------
                if (ligne.toLowerCase().startsWith("maison(")) {
                    if (etape > 1)
                        throw new IllegalArgumentException("ligne " + numeroLigne +
                                ") : maison après les connexions -> " + ligne);

                    etape = 1;

                    try {
                        parseMaison(ligne);
                    } catch (Exception e) {
                        throw new IllegalArgumentException(
                                "ligne " + numeroLigne + ") : " + e.getMessage()
                        );
                    }
                    continue;
                }

                // -------------------------
                // CONNEXION
                // -------------------------
                if (ligne.toLowerCase().startsWith("connexion(")) {
                    etape = 2;

                    try {
                        parseConnexion(ligne);
                    } catch (Exception e) {
                        throw new IllegalArgumentException(
                                "ligne " + numeroLigne + ") : " + e.getMessage()
                        );
                    }
                    continue;
                }

                // Si rien ne correspond
                throw new IllegalArgumentException(
                        "ligne " + numeroLigne + ") : syntaxe inconnue -> " + ligne
                );
            }

            // Vérification finale
            if (!isValide()) {
                throw new IllegalStateException(" Certaines maisons ne sont pas connectées )");
            }

        } catch (IOException e) {
            System.out.println("Erreur de lecture du fichier : " + e.getMessage());
        }
    }

    private void parseGenerateur(String ligne) {
        // format attendu : generateur(gen1,60)
        ligne = ligne.substring("generateur(".length(), ligne.length() - 1);
        String[] parts = ligne.split(",");

        if (parts.length != 2) {
            throw new IllegalArgumentException("Format générateur invalide : " + ligne);
        }

        String nom = parts[0];
        int capacite = Integer.parseInt(parts[1]);

        ajouterGenerateur(new Generateur(nom, capacite));
    }
    private void parseMaison(String ligne) {
        ligne = ligne.substring("maison(".length(), ligne.length() - 1);
        String[] parts = ligne.split(",");

        if (parts.length != 2) {
            throw new IllegalArgumentException("Format maison invalide : " + ligne);
        }

        String nom = parts[0];
        String type = parts[1].toUpperCase();

        TypeConso conso;

        try {
            conso = TypeConso.valueOf(type);
        } catch (Exception e) {
            throw new IllegalArgumentException("Type de consommation inconnu : " + type);
        }

        ajouterMaison(new Maison(nom, conso));
    }
    private void parseConnexion(String ligne) {
        ligne = ligne.substring("connexion(".length(), ligne.length() - 1);
        String[] parts = ligne.split(",");

        if (parts.length != 2) {
            throw new IllegalArgumentException("Format connexion invalide : " + ligne);
        }

        String a = parts[0].trim();
        String b = parts[1].trim();

        // ordre indifférent -> détecter qui est quoi
        Maison m = getMaisonParNom(a);
        Generateur g = getGenerateurParNom(b);

        if (m != null && g != null) {
            ajouterConnexion(m.getNom(), g.getNom());
            return;
        }

        // inversé ?
        m = getMaisonParNom(b);
        g = getGenerateurParNom(a);

        if (m != null && g != null) {
            ajouterConnexion(m.getNom(), g.getNom());
            return;
        }

        throw new IllegalArgumentException("Connexion impossible : " + a + " - " + b);
    }
    public Generateur getGenerateurAleatoire() {
        List<Generateur> liste = new ArrayList<>(connexions.keySet());
        if (liste.isEmpty()) return null;
        return liste.get(new Random().nextInt(liste.size()));
    }

    public Maison getMaisonAleatoire() {
        List<Maison> toutesLesMaison = new ArrayList<>();
        for (List<Maison> l : connexions.values()) {
            toutesLesMaison.addAll(l);
        }
        toutesLesMaison.addAll(maisonsNonConnectees);

        if (toutesLesMaison.isEmpty()) return null;
        return toutesLesMaison.get(new Random().nextInt(toutesLesMaison.size()));
    }

    public Reseau algoNaif(Reseau reseau, int k) {
        int i = 0;

        while (i < k) {
            Maison m = reseau.getMaisonAleatoire();
            Generateur g = reseau.getGenerateurAleatoire();

            if (m == null || g == null) break;

            // Sauvegarde pour retour arrière
            double ancienCout = reseau.calculerCout();

            // On tente un changement
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
    public static void sauvegarder(Reseau reseau, String path) throws IOException {
        try (FileWriter fw = new FileWriter(path)) {

            // 1) générateurs
            for (Generateur g : reseau.getConnexions().keySet()) {
                fw.write("generateur(" + g.getNom() + "," + g.getCapacite() + ").\n");
            }

            // 2) maisons (connectées + non connectées, sans doublons)
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

            // 3) connexions
            for (Generateur g : reseau.getConnexions().keySet()) {
                for (Maison m : reseau.getConnexions().get(g)) {
                    fw.write("connexion(" + g.getNom() + "," + m.getNom() + ").\n");
                }
            }
        }
    }
}

