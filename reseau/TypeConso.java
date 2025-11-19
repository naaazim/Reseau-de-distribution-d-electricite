package main.java.com.example.reseau;

public enum TypeConso {
    BASSE(10),NORMALE(20),FORTE(40);
    private final int consommation;
    private TypeConso(int consommation){
        this.consommation = consommation;
    }
    public int getConsommation(){ return consommation; }
}
