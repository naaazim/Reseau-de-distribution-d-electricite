package com.example.gui;

import com.example.reseau.*;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

import java.util.List;
import java.util.stream.Collectors;

import javafx.geometry.Point2D;
import java.util.ArrayList;
import javafx.scene.Node;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Line;
import java.util.HashMap;
import java.util.Map;

public class GuiMain extends Application {

    private Reseau reseau = new Reseau();

    // --- Composants UI ---
    private Pane networkDisplay = new Pane();
    private Label statusLabel = new Label("Prêt.");
    private ComboBox<String> maisonComboBox = new ComboBox<>();
    private ComboBox<String> generateurComboBox = new ComboBox<>();
    
    private Image maisonImage;
    private Image generateurImage;

    /**
     * Lance l'application JavaFX.
     *
     * @param args Les arguments de la ligne de commande (non utilisés dans cette application).
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Point d'entrée principal pour l'application JavaFX.
     * Initialise la fenêtre principale, configure l'interface utilisateur et affiche la scène.
     *
     * @param primaryStage La scène principale de l'application, fournie par JavaFX.
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Gestionnaire de Réseau Électrique");

        try {
            maisonImage = new Image(getClass().getResourceAsStream("maison.png"));
            generateurImage = new Image(getClass().getResourceAsStream("generateur.png"));
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de ressource", "Impossible de charger les images. Assurez-vous que 'maison.png' and 'generateur.png' sont dans le bon répertoire.");
            // On peut continuer sans images, l'UI sera moins jolie
        }


        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // --- Menu Bar ---
        MenuBar menuBar = createMenuBar(primaryStage);
        root.setTop(menuBar);

        // --- Affichage Central ---
        networkDisplay.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #a0a0a0;");
        root.setCenter(networkDisplay);

        // --- Panneau de Contrôle (Droite) ---
        ScrollPane controlPanel = createControlPanel(primaryStage);
        root.setRight(controlPanel);

        // --- Barre de Statut (Bas) ---
        HBox statusBar = new HBox(statusLabel);
        statusBar.setPadding(new Insets(5, 0, 0, 0));
        root.setBottom(statusBar);

        // Supprimer la redirection de System.out
        // System.setOut(new PrintStream(out, true));

        Scene scene = new Scene(root, 1024, 768);
        primaryStage.setScene(scene);
        primaryStage.show();

        updateUI();
    }

    /**
     * Crée et configure la barre de menu principale de l'application.
     * Le menu "Fichier" contient des options pour :
     * - Charger un réseau depuis un fichier.
     * - Sauvegarder le réseau actuel dans un fichier.
     * - Quitter l'application.
     *
     * @param owner La fenêtre parente (Stage), utilisée pour les boîtes de dialogue de fichier.
     * @return La barre de menu ({@link MenuBar}) configurée.
     */
    private MenuBar createMenuBar(Stage owner) {
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("Fichier");

        MenuItem loadItem = new MenuItem("Charger un réseau...");
        loadItem.setOnAction(e -> loadNetwork(owner));

        MenuItem saveItem = new MenuItem("Sauvegarder le réseau...");
        saveItem.setOnAction(e -> saveNetwork(owner));

        MenuItem quitItem = new MenuItem("Quitter");
        quitItem.setOnAction(e -> System.exit(0));

        fileMenu.getItems().addAll(loadItem, saveItem, new SeparatorMenuItem(), quitItem);
        menuBar.getMenus().add(fileMenu);
        return menuBar;
    }

    /**
     * Crée le panneau de contrôle latéral, qui contient toutes les commandes
     * pour interagir avec le réseau.
     * Ce panneau inclut des sections pour gérer les générateurs, les maisons,
     * les connexions et les actions globales.
     *
     * @param owner La fenêtre parente, nécessaire pour certaines actions comme le chargement/sauvegarde.
     * @return Un {@link ScrollPane} contenant le panneau de contrôle.
     */
    private ScrollPane createControlPanel(Stage owner) {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(10));
        // panel.setMinWidth(280); // La largeur est gérée par le ScrollPane

        panel.getChildren().addAll(
                createGeneratorPane(),
                createHousePane(),
                createConnectionPane(),
                createActionPane(owner));

        ScrollPane scrollPane = new ScrollPane(panel);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setMinWidth(310); // Largeur minimale pour le panneau défilant

        return scrollPane;
    }

    /**
     * Crée la section de l'interface dédiée à l'ajout de générateurs.
     * Cette section contient des champs pour le nom et la capacité, ainsi qu'un
     * bouton pour ajouter le générateur au réseau.
     *
     * @return Un {@link TitledPane} pour la gestion des générateurs.
     */
    private TitledPane createGeneratorPane() {
        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);

        TextField nomField = new TextField();
        nomField.setPromptText("Ex: G1");
        TextField capaciteField = new TextField();
        capaciteField.setPromptText("Ex: 100");
        Button addButton = new Button("Ajouter Générateur");

        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Capacité (kW):"), 0, 1);
        grid.add(capaciteField, 1, 1);

        VBox content = new VBox(10, grid, addButton);
        content.setPadding(new Insets(10));
        TitledPane titledPane = new TitledPane("Générateurs", content);

        addButton.setOnAction(e -> {
            String nom = nomField.getText();
            String capaciteStr = capaciteField.getText();
            if (nom.isEmpty() || capaciteStr.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Le nom et la capacité ne peuvent pas être vides.");
                return;
            }
            try {
                int capacite = Integer.parseInt(capaciteStr);
                reseau.ajouterGenerateur(new Generateur(nom, capacite));
                statusLabel.setText("Générateur " + nom + " ajouté/mis à jour.");
                nomField.clear();
                capaciteField.clear();
                updateUI();
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Erreur de format", "La capacité doit être un nombre entier.");
            } catch (IllegalArgumentException ex) {
                showAlert(Alert.AlertType.ERROR, "Erreur de valeur", ex.getMessage());
            }
        });

        return titledPane;
    }

    /**
     * Crée la section de l'interface dédiée à l'ajout de maisons.
     * Cette section contient des champs pour le nom et le type de consommation,
     * ainsi qu'un bouton pour ajouter la maison au réseau.
     *
     * @return Un {@link TitledPane} pour la gestion des maisons.
     */
    private TitledPane createHousePane() {
        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);

        TextField nomField = new TextField();
        nomField.setPromptText("Ex: M1");
        ComboBox<TypeConso> typeComboBox = new ComboBox<>(FXCollections.observableArrayList(TypeConso.values()));
        typeComboBox.setValue(TypeConso.NORMAL);
        Button addButton = new Button("Ajouter Maison");

        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Consommation:"), 0, 1);
        grid.add(typeComboBox, 1, 1);

        VBox content = new VBox(10, grid, addButton);
        content.setPadding(new Insets(10));
        TitledPane titledPane = new TitledPane("Maisons", content);

        addButton.setOnAction(e -> {
            String nom = nomField.getText();
            TypeConso type = typeComboBox.getValue();
            if (nom.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Le nom ne peut pas être vide.");
                return;
            }
            try {
                reseau.ajouterMaison(new Maison(nom, type));
                statusLabel.setText("Maison " + nom + " ajoutée/mise à jour.");
                nomField.clear();
                updateUI();
            } catch (IllegalArgumentException ex) {
                showAlert(Alert.AlertType.ERROR, "Erreur", ex.getMessage());
            }
        });

        return titledPane;
    }

    /**
     * Crée la section de l'interface dédiée à la gestion des connexions.
     * Permet de sélectionner une maison et un générateur depuis des listes
     * déroulantes pour les connecter ou les déconnecter.
     *
     * @return Un {@link TitledPane} pour la gestion des connexions.
     */
    private TitledPane createConnectionPane() {
        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);

        grid.add(new Label("Maison:"), 0, 0);
        grid.add(maisonComboBox, 1, 0);
        grid.add(new Label("Générateur:"), 0, 1);
        grid.add(generateurComboBox, 1, 1);

        Button addButton = new Button("Connecter");
        Button removeButton = new Button("Déconnecter");
        HBox buttons = new HBox(10, addButton, removeButton);
        buttons.setAlignment(Pos.CENTER);

        VBox content = new VBox(10, grid, buttons);
        content.setPadding(new Insets(10));
        TitledPane titledPane = new TitledPane("Connexions", content);

        addButton.setOnAction(e -> {
            String nomMaison = maisonComboBox.getValue();
            String nomGenerateur = generateurComboBox.getValue();
            if (nomMaison == null || nomGenerateur == null) {
                showAlert(Alert.AlertType.WARNING, "Sélection manquante",
                        "Veuillez sélectionner une maison et un générateur.");
                return;
            }
            try {
                reseau.ajouterConnexion(nomMaison, nomGenerateur);
                statusLabel.setText("Connexion ajoutée : " + nomMaison + " -> " + nomGenerateur);
                updateUI();
            } catch (IllegalArgumentException ex) {
                showAlert(Alert.AlertType.ERROR, "Erreur", ex.getMessage());
            }
        });

        removeButton.setOnAction(e -> {
            String nomMaison = maisonComboBox.getValue();
            String nomGenerateur = generateurComboBox.getValue();
            if (nomMaison == null || nomGenerateur == null) {
                showAlert(Alert.AlertType.WARNING, "Sélection manquante",
                        "Veuillez sélectionner une maison et un générateur.");
                return;
            }
            try {
                reseau.supprimerConnexion(nomMaison, nomGenerateur);
                statusLabel.setText("Connexion supprimée : " + nomMaison + " - " + nomGenerateur);
                updateUI();
            } catch (IllegalArgumentException ex) {
                showAlert(Alert.AlertType.ERROR, "Erreur", ex.getMessage());
            }
        });

        return titledPane;
    }

    /**
     * Crée la section "Actions" qui regroupe les fonctionnalités globales.
     * Cette section contient des boutons pour :
     * - Charger et sauvegarder le réseau.
     * - Calculer le coût en fonction d'une valeur lambda.
     * - Valider la topologie du réseau.
     * - Exécuter les algorithmes d'optimisation (naïf et avancé).
     *
     * @param owner La fenêtre parente, utilisée pour les boîtes de dialogue de fichier.
     * @return Un {@link TitledPane} pour les actions globales.
     */
    private TitledPane createActionPane(Stage owner) {
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        // --- Fichiers ---
        Button loadButton = new Button("Charger un réseau");
        loadButton.setMaxWidth(Double.MAX_VALUE);
        loadButton.setOnAction(e -> loadNetwork(owner));

        Button saveButton = new Button("Sauvegarder le réseau");
        saveButton.setMaxWidth(Double.MAX_VALUE);
        saveButton.setOnAction(e -> saveNetwork(owner));

        // --- Paramètres ---
        Label lambdaLabel = new Label("Sévérité (Lambda):");
        TextField lambdaField = new TextField(String.valueOf(reseau.getLambda()));

        Button costButton = new Button("Calculer le Coût");
        costButton.setMaxWidth(Double.MAX_VALUE);

        // --- Validation ---
        Button validateButton = new Button("Vérifier la validité");
        validateButton.setMaxWidth(Double.MAX_VALUE);

        // --- Optimisation Naïve ---
        Label kLabel = new Label("Itérations (k) pour Naïf:");
        TextField kField = new TextField("100");
        Button naiveButton = new Button("Optimisation Naïve");
        naiveButton.setMaxWidth(Double.MAX_VALUE);

        // --- Optimisation Avancée ---
        Button optimizeButton = new Button("Optimisation Avancée");
        optimizeButton.setMaxWidth(Double.MAX_VALUE);

        content.getChildren().addAll(
                loadButton,
                saveButton,
                new Separator(),
                lambdaLabel, lambdaField,
                costButton,
                validateButton,
                new Separator(),
                kLabel, kField, naiveButton,
                new Separator(),
                optimizeButton);

        TitledPane titledPane = new TitledPane("Actions", content);

        costButton.setOnAction(e -> {
            try {
                int lambda = Integer.parseInt(lambdaField.getText());
                reseau.setLambda(lambda);
                double cout = reseau.calculerCout();
                String costString = String.format("Coût actuel du réseau : %.4f", cout);
                statusLabel.setText(costString);
                showAlert(Alert.AlertType.INFORMATION, "Résultat du Calcul", costString);
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "La sévérité (Lambda) doit être un entier.");
            }
        });

        validateButton.setOnAction(e -> {
            if (reseau.isValide()) {
                showAlert(Alert.AlertType.INFORMATION, "Validité",
                        "Le réseau est VALIDE. Toutes les maisons sont connectées.");
                statusLabel.setText("Réseau valide.");
            } else {
                showAlert(Alert.AlertType.WARNING, "Validité",
                        "Le réseau est INVALIDE. Certaines maisons ne sont pas connectées.");
                statusLabel.setText("Réseau invalide !");
            }
        });

        naiveButton.setOnAction(e -> {
            if (reseau.getConnexions().isEmpty() && reseau.getMaisonsNonConnectees().isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "Optimisation", "Le réseau est vide.");
                return;
            }
            try {
                int lambda = Integer.parseInt(lambdaField.getText());
                reseau.setLambda(lambda);
                int k = Integer.parseInt(kField.getText());
                reseau = reseau.algoNaif(reseau, k);
                updateUI();
                statusLabel.setText("Optimisation naïve terminée (" + k + " itérations). Coût: "
                        + String.format("%.4f", reseau.calculerCout()));
                showAlert(Alert.AlertType.INFORMATION, "Optimisation Naïve", "Optimisation terminée.");
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Erreur",
                        "Le nombre d'itérations (k) et Lambda doivent être des entiers.");
            }
        });

        optimizeButton.setOnAction(e -> {
            if (reseau.getConnexions().isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "Optimisation", "Le réseau est vide, optimisation impossible.");
                return;
            }
            try {
                int lambda = Integer.parseInt(lambdaField.getText());
                reseau.setLambda(lambda);
                reseau = Reseau.algoOptimise(reseau);
                updateUI();
                statusLabel.setText("Réseau optimisé (Avancé). Coût: " + String.format("%.4f", reseau.calculerCout()));
                showAlert(Alert.AlertType.INFORMATION, "Optimisation", "L'optimisation du réseau est terminée.");
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "La sévérité (Lambda) doit être un entier.");
            }
        });

        return titledPane;
    }

    /**
     * Met à jour tous les composants de l'interface utilisateur pour refléter l'état
     * actuel du {@link Reseau}.
     * Cela inclut l'affichage graphique du réseau, les listes déroulantes pour
     * les maisons et générateurs, et la barre de statut.
     */
    private void updateUI() {
        networkDisplay.getChildren().clear();

        Map<String, Node> nodeMap = new HashMap<>();
        Map<String, Point2D> positions = new HashMap<>();


        // Layout constants
        double genY = 80;
        double houseY = 400;
        double spacing = 120;
        double imageSize = 64;

        // --- 1. Dessiner les générateurs ---
        List<Generateur> generateurs = new ArrayList<>(reseau.getConnexions().keySet());
        double totalGenWidth = generateurs.size() * spacing;
        double genStartX = (networkDisplay.getWidth() - totalGenWidth) / 2 + spacing / 2;
        if (genStartX < 40) genStartX = 40;

        for (int i = 0; i < generateurs.size(); i++) {
            Generateur g = generateurs.get(i);
            double x = genStartX + i * spacing;
            Point2D center = new Point2D(x, genY);

            ImageView imageView = new ImageView(generateurImage);
            imageView.setFitWidth(imageSize);
            imageView.setFitHeight(imageSize);
            imageView.setX(center.getX() - imageSize / 2);
            imageView.setY(center.getY() - imageSize / 2);

            Label label = new Label(g.getNom() + "\n(" + g.getCapacite() + " kW)");
            label.setAlignment(Pos.CENTER);
            label.setLayoutX(center.getX() - 30);
            label.setLayoutY(center.getY() - imageSize / 2 - 40);

            networkDisplay.getChildren().addAll(imageView, label);
            nodeMap.put(g.getNom(), imageView);
            positions.put(g.getNom(), center);
        }

        // --- 2. Dessiner les maisons (connectées et non connectées) ---
        List<Maison> toutesLesMaisons = new ArrayList<>();
        reseau.getConnexions().values().forEach(toutesLesMaisons::addAll);
        toutesLesMaisons.addAll(reseau.getMaisonsNonConnectees());

        double totalHouseWidth = toutesLesMaisons.size() * spacing;
        double houseStartX = (networkDisplay.getWidth() - totalHouseWidth) / 2 + spacing / 2;
        if (houseStartX < 40) houseStartX = 40;

        for (int i = 0; i < toutesLesMaisons.size(); i++) {
            Maison m = toutesLesMaisons.get(i);
            double x = houseStartX + i * spacing;
            Point2D center = new Point2D(x, houseY);

            ImageView imageView = new ImageView(maisonImage);
            imageView.setFitWidth(imageSize);
            imageView.setFitHeight(imageSize);
            imageView.setX(center.getX() - imageSize / 2);
            imageView.setY(center.getY() - imageSize / 2);
            
            Label label = new Label(m.getNom() + "\n(" + m.getTypeConso().getConsommation() + " kW)");
            label.setAlignment(Pos.CENTER);
            label.setLayoutX(center.getX() - 20);
            label.setLayoutY(center.getY() + imageSize / 2 + 5);

            networkDisplay.getChildren().addAll(imageView, label);
            nodeMap.put(m.getNom(), imageView);
            positions.put(m.getNom(), center);
        }

        // --- 3. Dessiner les connexions ---
        for (Map.Entry<Generateur, List<Maison>> entry : reseau.getConnexions().entrySet()) {
            Point2D genPos = positions.get(entry.getKey().getNom());
            if (genPos == null) continue;

            for (Maison m : entry.getValue()) {
                Point2D housePos = positions.get(m.getNom());
                if (housePos == null) continue;

                Line line = new Line(genPos.getX(), genPos.getY(), housePos.getX(), housePos.getY());
                line.setStrokeWidth(2);

                // Mettre la ligne en arrière-plan
                networkDisplay.getChildren().add(0, line);
            }
        }


        // --- 4. Mettre à jour les ComboBoxes et le statut ---
        maisonComboBox.setItems(FXCollections.observableArrayList(
                toutesLesMaisons.stream()
                        .map(Maison::getNom)
                        .collect(Collectors.toList())));

        generateurComboBox.setItems(FXCollections.observableArrayList(
                generateurs.stream()
                        .map(Generateur::getNom)
                        .collect(Collectors.toList())));

        double cout = reseau.calculerCout();
        statusLabel.setText(String.format("Coût actuel: %.4f", cout));
    }

    /**
     * Ouvre une boîte de dialogue permettant à l'utilisateur de sélectionner un
     * fichier pour charger une configuration de réseau.
     * Le réseau actuel est réinitialisé et remplacé par celui du fichier.
     *
     * @param owner La fenêtre parente qui possède la boîte de dialogue.
     */
    private void loadNetwork(Stage owner) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Charger un fichier réseau");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers Texte", "*.txt"));
        File file = fileChooser.showOpenDialog(owner);

        if (file != null) {
            try {
                reseau = new Reseau(); // Re-initialise le réseau
                reseau.chargerReseauDepuisFichier(file.getAbsolutePath());
                updateUI();
                statusLabel.setText("Réseau chargé depuis " + file.getName());
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur de chargement",
                        "Impossible de charger le fichier : \n" + e.getMessage());
            }
        }
    }

    /**
     * Ouvre une boîte de dialogue pour permettre à l'utilisateur de sauvegarder
     * la configuration actuelle du réseau dans un fichier texte.
     *
     * @param owner La fenêtre parente qui possède la boîte de dialogue.
     */
    private void saveNetwork(Stage owner) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sauvegarder le réseau");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers Texte", "*.txt"));
        File file = fileChooser.showSaveDialog(owner);

        if (file != null) {
            try {
                Reseau.sauvegarder(reseau, file.getAbsolutePath());
                statusLabel.setText("Réseau sauvegardé dans " + file.getName());
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur de sauvegarde",
                        "Impossible de sauvegarder le fichier : \n" + e.getMessage());
            }
        }
    }

    /**
     * Affiche une boîte de dialogue modale (alerte) à l'utilisateur.
     *
     * @param type    Le type d'alerte (par exemple, {@code Alert.AlertType.ERROR}).
     * @param title   Le titre de la fenêtre d'alerte.
     * @param content Le message à afficher dans l'alerte.
     */
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
