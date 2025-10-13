package com.example.reseau;

public class Maison {
    private String nom;
    private TypeConso typeConso;
    public Maison(String nom, TypeConso typeConso){
        this.nom = nom;
        this.typeConso = typeConso;
    }
    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
    public TypeConso getTypeConso() {
        return typeConso;
    }
    public void setTypeConso(TypeConso typeConso) {
        this.typeConso = typeConso;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Maison maison = (Maison) obj;
        return nom.equalsIgnoreCase(maison.nom);
    }
    @Override
    public String toString() {
        return nom + " (" + typeConso + " - " + typeConso.getConsommation() + " kW)";
    }

    public void afficher(){
        System.out.println(this);
    }
}
