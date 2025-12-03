package com.example.reseau;

/**
 * Représente un générateur d'électricité dans le réseau.
 * Chaque générateur est défini par un nom unique (insensible à la casse) et une capacité de production.
 */
public class Generateur {
    private String nom;
    private int capacite;

    /**
     * Construit un nouveau générateur avec un nom et une capacité spécifiés.
     *
     * @param nom Le nom du générateur.
     * @param capacite La capacité de production en kW (doit être positive).
     */
    public Generateur(String nom, int capacite) {
        if (capacite <= 0)
            throw new IllegalArgumentException("  La capacité doit être positive !");
        this.nom = nom;
        this.capacite = capacite;
    }

    /**
     * Retourne le nom du générateur.
     *
     * @return Le nom du générateur.
     */
    public String getNom() {
        return nom;
    }

    /**
     * Met à jour le nom du générateur.
     *
     * @param nom Le nouveau nom.
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * Retourne la capacité de production du générateur.
     *
     * @return La capacité de production du générateur en kW.
     */
    public int getCapacite() {
        return capacite;
    }

    /**
     * Met à jour la capacité de production du générateur.
     *
     * @param capacite La nouvelle capacité en kW (doit être positive).
     */
    public void setCapacite(int capacite) {
        if (capacite <= 0) {
            throw new IllegalArgumentException("  La capacité doit être positive !");
        }
        this.capacite = capacite;
        System.out.println("La capacité du générateur " + this.nom + " a été modifiée.");
    }

    /**
     * Calcule le hash code basé sur le nom du générateur (insensible à la casse).
     *
     * @return Le hash code de l'objet.
     */
    @Override
    public int hashCode() {
        return nom == null ? 0 : nom.toLowerCase().hashCode();
    }

    /**
     * Compare cet objet avec un autre pour vérifier l'égalité.
     * Deux générateurs sont considérés comme égaux si leurs noms sont les mêmes (insensible à la casse).
     *
     * @param obj L'objet à comparer.
     * @return {@code true} si les objets sont égaux, {@code false} sinon.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Generateur generateur = (Generateur) obj;
        return nom.equalsIgnoreCase(generateur.nom);
    }

    /**
     * Retourne une représentation textuelle du générateur.
     *
     * @return Une chaîne de caractères représentant le générateur, incluant son nom et sa capacité.
     */
    @Override
    public String toString() {
        return nom + " (" + capacite + " kW)";
    }

    /**
     * Affiche la représentation textuelle du générateur sur la sortie standard.
     */
    public void afficher() {
        System.out.print(this);
    }
}
