package com.example.reseau;

/**
 * Énumération des différents types de consommation électrique pour une maison.
 * Chaque type est associé à une valeur de consommation fixe en kW.
 */
public enum TypeConso {
    BASSE(10), NORMAL(20), FORTE(40);

    private final int consommation;

    /**
     * Constructeur privé pour initialiser chaque constante avec sa valeur de consommation.
     * @param consommation La consommation en kW.
     */
    private TypeConso(int consommation) {
        this.consommation = consommation;
    }

    /**
     * @return La valeur de la consommation en kW pour ce type.
     */
    public int getConsommation() {
        return consommation;
    }
}
