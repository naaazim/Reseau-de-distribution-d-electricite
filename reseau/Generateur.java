package main.java.com.example.reseau;

public class Generateur{
    private String nom; 
    private int capacite;
    public Generateur(String nom, int capacite) {
        if (capacite <= 0) throw new IllegalArgumentException("  La capacité doit être positive !");
        this.nom = nom;
        this.capacite = capacite;
    }
    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
    public int getCapacite() {
        return capacite;
    }
    public void setCapacite(int capacite){
        if (capacite <= 0) {
            throw new IllegalArgumentException("  La capacité doit être positive !");
        }
        this.capacite = capacite;
    }
    @Override
    public int hashCode() {
        return nom == null ? 0 : nom.toLowerCase().hashCode();
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Generateur generateur = (Generateur)obj;
        return nom.equalsIgnoreCase(generateur.nom);
    }
    @Override
    public String toString() {
        return nom + " (" + capacite + " kW)";
    }
   
    public void afficher(){
        System.out.print(this);
    }
}
