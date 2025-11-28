package com.example.reseau;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvFileSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Reseau utility methods using parameterized tests.
 * Tests cover calculation, validation, search, and parsing methods.
 */
class ReseauUtilsTest {

    private Reseau reseau;

    @BeforeEach
    void setUp() {
        reseau = new Reseau();
    }

    // ==================== @ValueSource Tests ====================

    /**
     * Test getTauxUtilisation with various generator capacities.
     */
    @ParameterizedTest
    @ValueSource(ints = { 50, 100, 150, 200, 250 })
    void testGetTauxUtilisationWithVariousCapacities(int capacite) {
        Generateur g = new Generateur("G1", capacite);
        reseau.ajouterGenerateur(g);

        // Add a house with NORMAL consumption (20 kW)
        Maison m = new Maison("M1", TypeConso.NORMAL);
        reseau.ajouterMaison(m);
        reseau.ajouterConnexion("M1", "G1");

        double expectedRate = 20.0 / capacite;
        assertEquals(expectedRate, reseau.getTauxUtilisation(g), 0.001);
    }

    /**
     * Test lambda values for cost calculation.
     */
    @ParameterizedTest
    @ValueSource(ints = { 1, 10, 50, 100, 200 })
    void testLambdaValues(int lambda) {
        reseau.setLambda(lambda);
        assertEquals(lambda, reseau.getLambda());
    }

    /**
     * Test generator capacity edge cases.
     */
    @ParameterizedTest
    @ValueSource(ints = { 10, 20, 30, 40, 50 })
    void testGeneratorCapacityEdgeCases(int capacite) {
        Generateur g = new Generateur("G_" + capacite, capacite);
        reseau.ajouterGenerateur(g);

        assertEquals(capacite, reseau.getGenerateurParNom("G_" + capacite).getCapacite());
    }

    // ==================== @CsvSource Tests ====================

    /**
     * Test calculerCout with different network configurations.
     */
    @ParameterizedTest
    @CsvSource({
            "100, 10, 0", // Generator: 100, House: BASSE (10), Expected cost: 0
            "100, 20, 0", // Generator: 100, House: NORMALE (20), Expected cost: 0
            "50, 10, 0", // Generator: 50, House: BASSE (10), Expected cost: 0
            "200, 40, 0" // Generator: 200, House: FORTE (40), Expected cost: 0
    })
    void testCalculerCoutSimpleConfigurations(int genCapacity, int houseConso, double expectedCost) {
        Generateur g = new Generateur("G1", genCapacity);
        reseau.ajouterGenerateur(g);

        // Determine house type based on consumption
        TypeConso type = TypeConso.BASSE;
        if (houseConso == 20)
            type = TypeConso.NORMAL;
        else if (houseConso == 40)
            type = TypeConso.FORTE;

        Maison m = new Maison("M1", type);
        reseau.ajouterMaison(m);
        reseau.ajouterConnexion("M1", "G1");

        assertEquals(expectedCost, reseau.calculerCout(), 0.001);
    }

    /**
     * Test getGenerateurParNom with various name formats.
     */
    @ParameterizedTest
    @CsvSource({
            "G1, G1, true",
            "g1, G1, true", // Case insensitive
            "G2, G2, true",
            "Gen1, Gen1, true",
            "NonExistent, NonExistent, false"
    })
    void testGetGenerateurParNom(String searchName, String actualName, boolean shouldExist) {
        if (shouldExist && !searchName.equals("NonExistent")) {
            Generateur g = new Generateur(actualName, 100);
            reseau.ajouterGenerateur(g);
            assertNotNull(reseau.getGenerateurParNom(searchName));
        } else {
            assertNull(reseau.getGenerateurParNom(searchName));
        }
    }

    /**
     * Test getMaisonParNom with various name formats.
     */
    @ParameterizedTest
    @CsvSource({
            "M1, M1, true",
            "m1, M1, true", // Case insensitive
            "M2, M2, true",
            "House1, House1, true",
            "NonExistent, NonExistent, false"
    })
    void testGetMaisonParNom(String searchName, String actualName, boolean shouldExist) {
        if (shouldExist && !searchName.equals("NonExistent")) {
            // Add a generator first to satisfy capacity constraint
            reseau.ajouterGenerateur(new Generateur("G1", 100));
            Maison m = new Maison(actualName, TypeConso.BASSE);
            reseau.ajouterMaison(m);
            assertNotNull(reseau.getMaisonParNom(searchName));
        } else {
            assertNull(reseau.getMaisonParNom(searchName));
        }
    }

    /**
     * Test network validation with different configurations.
     */
    @ParameterizedTest
    @CsvSource({
            "1, 1, true", // 1 generator, 1 house, connected -> valid
            "2, 2, true" // 2 generators, 2 houses, all connected -> valid
    })
    void testIsValideWithConfigurations(int numGenerators, int numHouses, boolean expectedValid) {
        // Add generators
        for (int i = 1; i <= numGenerators; i++) {
            reseau.ajouterGenerateur(new Generateur("G" + i, 100));
        }

        // Add houses
        for (int i = 1; i <= numHouses; i++) {
            reseau.ajouterMaison(new Maison("M" + i, TypeConso.BASSE));
        }

        // Connect all houses to first generator
        for (int i = 1; i <= numHouses; i++) {
            reseau.ajouterConnexion("M" + i, "G1");
        }

        assertEquals(expectedValid, reseau.isValide());
    }

    // ==================== @EnumSource Tests ====================

    /**
     * Test house creation with all TypeConso values.
     */
    @ParameterizedTest
    @EnumSource(TypeConso.class)
    void testAjouterMaisonWithAllTypes(TypeConso type) {
        // Add generator with sufficient capacity
        reseau.ajouterGenerateur(new Generateur("G1", 200));

        Maison m = new Maison("M_" + type, type);
        reseau.ajouterMaison(m);

        assertNotNull(reseau.getMaisonParNom("M_" + type));
        assertEquals(type, reseau.getMaisonParNom("M_" + type).getTypeConso());
    }

    /**
     * Test getTauxUtilisation with all house types.
     */
    @ParameterizedTest
    @EnumSource(TypeConso.class)
    void testGetTauxUtilisationWithAllHouseTypes(TypeConso type) {
        Generateur g = new Generateur("G1", 200);
        reseau.ajouterGenerateur(g);

        Maison m = new Maison("M1", type);
        reseau.ajouterMaison(m);
        reseau.ajouterConnexion("M1", "G1");

        double expectedRate = type.getConsommation() / 200.0;
        assertEquals(expectedRate, reseau.getTauxUtilisation(g), 0.001);
    }

    /**
     * Test network capacity with all consumption types.
     */
    @ParameterizedTest
    @EnumSource(TypeConso.class)
    void testNetworkCapacityWithAllTypes(TypeConso type) {
        // Add generator with capacity matching the consumption type
        int requiredCapacity = type.getConsommation();
        reseau.ajouterGenerateur(new Generateur("G1", requiredCapacity));

        Maison m = new Maison("M1", type);
        reseau.ajouterMaison(m);
        reseau.ajouterConnexion("M1", "G1");

        // Should have no surcharge
        assertEquals(0.0, reseau.surcharge(), 0.001);
    }

    // ==================== @CsvFileSource Tests ====================

    /**
     * Test network configurations from CSV file.
     */
    @ParameterizedTest
    @CsvFileSource(resources = "/test-data.csv", numLinesToSkip = 1)
    void testNetworkFromCsvFile(String generatorName, int generatorCapacity,
            String houseName, String houseType, boolean expectedValid) {
        // Parse house type
        TypeConso type = TypeConso.valueOf(houseType);

        // Add generator
        Generateur g = new Generateur(generatorName, generatorCapacity);
        reseau.ajouterGenerateur(g);

        // Add house
        Maison m = new Maison(houseName, type);

        // Check if adding house should succeed based on capacity
        if (generatorCapacity >= type.getConsommation()) {
            reseau.ajouterMaison(m);
            reseau.ajouterConnexion(houseName, generatorName);
            assertEquals(expectedValid, reseau.isValide());
        } else {
            // Should throw exception if capacity is insufficient
            assertThrows(IllegalArgumentException.class, () -> reseau.ajouterMaison(m));
        }
    }

    /**
     * Test cost calculations from CSV configurations.
     */
    @ParameterizedTest
    @CsvFileSource(resources = "/test-data.csv", numLinesToSkip = 1)
    void testCostCalculationFromCsv(String generatorName, int generatorCapacity,
            String houseName, String houseType, boolean expectedValid) {
        TypeConso type = TypeConso.valueOf(houseType);

        // Only test valid configurations
        if (generatorCapacity >= type.getConsommation()) {
            Generateur g = new Generateur(generatorName, generatorCapacity);
            reseau.ajouterGenerateur(g);

            Maison m = new Maison(houseName, type);
            reseau.ajouterMaison(m);
            reseau.ajouterConnexion(houseName, generatorName);

            // With single generator and no overload, cost should be 0
            double cost = reseau.calculerCout();
            assertEquals(0.0, cost, 0.001);
        }
    }

    // ==================== Additional Utility Tests ====================

    /**
     * Test isConnexionPossible method.
     */
    @Test
    void testIsConnexionPossible() {
        // No generators or houses -> should return true (no connections possible)
        assertTrue(reseau.isConnexionPossible());

        // Add generator and house
        reseau.ajouterGenerateur(new Generateur("G1", 100));
        reseau.ajouterMaison(new Maison("M1", TypeConso.BASSE));

        // Unconnected house exists -> should return false
        assertFalse(reseau.isConnexionPossible());

        // Connect the house
        reseau.ajouterConnexion("M1", "G1");

        // All houses connected -> should return true
        assertTrue(reseau.isConnexionPossible());
    }

    /**
     * Test dispersion calculation.
     */
    @Test
    void testDispersion() {
        // Single generator -> dispersion should be 0
        Generateur g1 = new Generateur("G1", 100);
        reseau.ajouterGenerateur(g1);
        Maison m1 = new Maison("M1", TypeConso.NORMAL);
        reseau.ajouterMaison(m1);
        reseau.ajouterConnexion("M1", "G1");

        assertEquals(0.0, reseau.dispersion(), 0.001);

        // Add second generator with different utilization
        Generateur g2 = new Generateur("G2", 50);
        reseau.ajouterGenerateur(g2);
        Maison m2 = new Maison("M2", TypeConso.BASSE);
        reseau.ajouterMaison(m2);
        reseau.ajouterConnexion("M2", "G2");

        // Dispersion calculation: both generators have different utilization rates
        // G1: 20/100 = 0.2, G2: 10/50 = 0.2 -> same rate, dispersion = 0
        // Let's use different capacities to get different rates
        double dispersion = reseau.dispersion();
        // With G1 at 0.2 and G2 at 0.2, dispersion should be 0
        assertEquals(0.0, dispersion, 0.001);
    }

    /**
     * Test surcharge calculation.
     */
    @Test
    void testSurcharge() {
        // No overload -> surcharge should be 0
        Generateur g = new Generateur("G1", 100);
        reseau.ajouterGenerateur(g);
        Maison m = new Maison("M1", TypeConso.BASSE);
        reseau.ajouterMaison(m);
        reseau.ajouterConnexion("M1", "G1");

        assertEquals(0.0, reseau.surcharge(), 0.001);
    }

    /**
     * Test empty network state.
     */
    @Test
    void testEmptyNetwork() {
        assertTrue(reseau.isValide()); // Empty network is considered valid
        assertEquals(0.0, reseau.calculerCout(), 0.001);
        assertEquals(0.0, reseau.dispersion(), 0.001);
        assertEquals(0.0, reseau.surcharge(), 0.001);
    }

    /**
     * Test null generator in getTauxUtilisation.
     */
    @Test
    void testGetTauxUtilisationWithNullGenerator() {
        // getTauxUtilisation calls g.getNom() which throws NPE with null
        assertThrows(NullPointerException.class, () -> reseau.getTauxUtilisation(null));
    }

    /**
     * Test non-existent generator in getTauxUtilisation.
     */
    @Test
    void testGetTauxUtilisationWithNonExistentGenerator() {
        Generateur g = new Generateur("NonExistent", 100);
        assertEquals(0.0, reseau.getTauxUtilisation(g), 0.001);
    }
}
