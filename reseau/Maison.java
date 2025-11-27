package com.example.reseau;

/**
 * Représente une maison dans le réseau électrique.
 * Chaque maison est définie par un nom unique (insensible à la casse) et un type de consommation.
 */
public class Maison {
    private String nom;
    private TypeConso typeConso;

    /**
     * Construit une nouvelle maison avec un nom et un type de consommation.
     *
     * @param nom Le nom de la maison.
     * @param typeConso Le type de consommation (BASSE, NORMAL, FORTE).
     */
    public Maison(String nom, TypeConso typeConso) {
        this.nom = nom;
        this.typeConso = typeConso;
    }

    /**
     * @return Le nom de la maison.
     */
    public String getNom() {
        return nom;
    }

    /**
     * Met à jour le nom de la maison.
     *
     * @param nom Le nouveau nom de la maison.
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * @return Le type de consommation de la maison.
     */
    public TypeConso getTypeConso() {
        return typeConso;
    }

    /**
     * Met à jour le type de consommation de la maison.
     *
     * @param typeConso Le nouveau type de consommation.
     */
    public void setTypeConso(TypeConso typeConso) {
        this.typeConso = typeConso;
    }

    /**
     * Compare cet objet avec un autre pour vérifier l'égalité.
     * Deux maisons sont considérées comme égales si leurs noms sont les mêmes (insensible à la casse).
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
        Maison maison = (Maison) obj;
        return nom.equalsIgnoreCase(maison.nom);
    }

    /**
     * @return Une représentation textuelle de la maison, incluant son nom et sa consommation.
     */
    @Override
    public String toString() {
        return nom + " (" + typeConso + " - " + typeConso.getConsommation() + " kW)";
    }

    /**
     * Affiche la représentation textuelle de la maison sur la sortie standard, suivie d'un saut de ligne.
     */
    public void afficher() {
        System.out.println(this);
    }

    /**
     * Calcule le hash code basé sur le nom de la maison (insensible à la casse).
     *
     * @return Le hash code de l'objet.
     */
    @Override
    public int hashCode() {
        return nom == null ? 0 : nom.toLowerCase().hashCode();
    }

}
