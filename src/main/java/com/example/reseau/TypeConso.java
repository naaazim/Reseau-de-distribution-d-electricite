package com.example.reseau;

/**
 * Énumération des différents types de consommation électrique pour une maison.
 * Chaque type est associé à une valeur de consommation fixe en kW.
 */
public enum TypeConso {
    BASSE(10), NORMAL(20), FORTE(40);

    private final int consommation;

    /**
     * Constructeur pour initialiser chaque constante avec sa valeur de consommation.
     * @param consommation La consommation en kW.
     */
    TypeConso(int consommation) {
        this.consommation = consommation;
    }

    /**
     * Retourne la valeur de la consommation pour ce type.
     *
     * @return La valeur de la consommation en kW.
     */
    public int getConsommation() {
        return consommation;
    }
}
