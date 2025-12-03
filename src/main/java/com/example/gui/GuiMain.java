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

/**
 * Classe principale de l'interface graphique (GUI) pour la gestion du réseau électrique.
 * Elle utilise JavaFX pour fournir une représentation visuelle et interactive du réseau.
 */
public class GuiMain extends Application {

    private Reseau reseau = new Reseau();

    private Pane networkDisplay = new Pane();
    private Label statusLabel = new Label("Prêt.");
    private ComboBox<String> maisonComboBox = new ComboBox<>();
    private ComboBox<String> generateurComboBox = new ComboBox<>();
    
    private Image maisonBasseImage;
    private Image maisonNormaleImage;
    private Image maisonForteImage;
    private Image generateurImage;

    /**
     * Lance l'application JavaFX.
     *
     * @param args Les arguments de la ligne de commande (non utilisés).
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Point d'entrée principal pour l'application JavaFX.
     * Initialise la fenêtre principale (Stage), configure l'interface utilisateur et affiche la scène.
     *
     * @param primaryStage La scène principale de l'application.
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Gestionnaire de Réseau Électrique");

        try {
            maisonBasseImage = new Image(getClass().getResourceAsStream("maisonBasse.png"));
            maisonNormaleImage = new Image(getClass().getResourceAsStream("maisonNormale.png"));
            maisonForteImage = new Image(getClass().getResourceAsStream("maisonForte.png"));
            generateurImage = new Image(getClass().getResourceAsStream("generateur.png"));

            if (maisonBasseImage.isError() || maisonNormaleImage.isError() || maisonForteImage.isError() || generateurImage.isError()) {
                throw new Exception("Une ou plusieurs images n'ont pas pu être chargées.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de ressource", "Impossible de charger les images. Assurez-vous que les fichiers .png sont dans le bon répertoire de ressources.");
        }


        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        MenuBar menuBar = createMenuBar(primaryStage);
        root.setTop(menuBar);

        networkDisplay.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #a0a0a0;");
        ScrollPane scrollPane = new ScrollPane(networkDisplay);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setFitToHeight(true);
        root.setCenter(scrollPane);

        ScrollPane controlPanel = createControlPanel(primaryStage);
        root.setRight(controlPanel);

        HBox statusBar = new HBox(statusLabel);
        statusBar.setPadding(new Insets(5, 0, 0, 0));
        root.setBottom(statusBar);

        Scene scene = new Scene(root, 1024, 768);
        primaryStage.setScene(scene);
        primaryStage.show();

        updateUI();
    }

    /**
     * Crée et configure la barre de menu principale de l'application.
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
     * Crée le panneau de contrôle latéral qui contient toutes les commandes pour interagir avec le réseau.
     *
     * @param owner La fenêtre parente, nécessaire pour certaines actions.
     * @return Un {@link ScrollPane} contenant le panneau de contrôle.
     */
    private ScrollPane createControlPanel(Stage owner) {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(10));

        panel.getChildren().addAll(
                createGeneratorPane(),
                createHousePane(),
                createConnectionPane(),
                createActionPane(owner));

        ScrollPane scrollPane = new ScrollPane(panel);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setMinWidth(310);

        return scrollPane;
    }

    /**
     * Crée la section de l'interface dédiée à la gestion des générateurs.
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
     * Crée la section de l'interface dédiée à la gestion des maisons.
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
                showAlert(Alert.AlertType.WARNING, "Sélection manquante", "Veuillez sélectionner une maison et un générateur.");
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
                showAlert(Alert.AlertType.WARNING, "Sélection manquante", "Veuillez sélectionner une maison et un générateur.");
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
     *
     * @param owner La fenêtre parente, utilisée pour les boîtes de dialogue.
     * @return Un {@link TitledPane} pour les actions globales.
     */
    private TitledPane createActionPane(Stage owner) {
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        Button loadButton = new Button("Charger un réseau");
        loadButton.setMaxWidth(Double.MAX_VALUE);
        loadButton.setOnAction(e -> loadNetwork(owner));

        Button saveButton = new Button("Sauvegarder le réseau");
        saveButton.setMaxWidth(Double.MAX_VALUE);
        saveButton.setOnAction(e -> saveNetwork(owner));

        Label lambdaLabel = new Label("Sévérité (Lambda):");
        TextField lambdaField = new TextField(String.valueOf(reseau.getLambda()));
        Button costButton = new Button("Calculer le Coût");
        costButton.setMaxWidth(Double.MAX_VALUE);

        Button validateButton = new Button("Vérifier la validité");
        validateButton.setMaxWidth(Double.MAX_VALUE);

        Label kLabel = new Label("Itérations (k) pour Naïf:");
        TextField kField = new TextField("100");
        Button naiveButton = new Button("Optimisation Naïve");
        naiveButton.setMaxWidth(Double.MAX_VALUE);

        Button optimizeButton = new Button("Optimisation Avancée");
        optimizeButton.setMaxWidth(Double.MAX_VALUE);

        content.getChildren().addAll(
                loadButton, saveButton, new Separator(),
                lambdaLabel, lambdaField, costButton, validateButton, new Separator(),
                kLabel, kField, naiveButton, new Separator(),
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
                showAlert(Alert.AlertType.INFORMATION, "Validité", "Le réseau est VALIDE. Toutes les maisons sont connectées.");
                statusLabel.setText("Réseau valide.");
            } else {
                showAlert(Alert.AlertType.WARNING, "Validité", "Le réseau est INVALIDE. Certaines maisons ne sont pas connectées.");
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
                statusLabel.setText("Optimisation naïve terminée (" + k + " itérations). Coût: " + String.format("%.4f", reseau.calculerCout()));
                showAlert(Alert.AlertType.INFORMATION, "Optimisation Naïve", "Optimisation terminée.");
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Le nombre d'itérations (k) et Lambda doivent être des entiers.");
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
     * Met à jour tous les composants de l'interface pour refléter l'état actuel du {@link Reseau}.
     * Redessine l'affichage graphique et met à jour les listes déroulantes.
     */
    private void updateUI() {
        networkDisplay.getChildren().clear();

        Map<String, Node> nodeMap = new HashMap<>();
        Map<String, Point2D> positions = new HashMap<>();

        double genY = 80;
        double houseY = 400;
        double spacing = 120;
        double imageSize = 64;

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

            ImageView imageView = new ImageView(getImageForMaison(m));
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

        for (Map.Entry<Generateur, List<Maison>> entry : reseau.getConnexions().entrySet()) {
            Point2D genPos = positions.get(entry.getKey().getNom());
            if (genPos == null) continue;

            for (Maison m : entry.getValue()) {
                Point2D housePos = positions.get(m.getNom());
                if (housePos == null) continue;

                Line line = new Line(genPos.getX(), genPos.getY(), housePos.getX(), housePos.getY());
                line.setStrokeWidth(2);
                networkDisplay.getChildren().add(0, line);
            }
        }

        maisonComboBox.setItems(FXCollections.observableArrayList(
                toutesLesMaisons.stream().map(Maison::getNom).collect(Collectors.toList())));

        generateurComboBox.setItems(FXCollections.observableArrayList(
                generateurs.stream().map(Generateur::getNom).collect(Collectors.toList())));

        double cout = reseau.calculerCout();
        statusLabel.setText(String.format("Coût actuel: %.4f", cout));
    }

    /**
     * Sélectionne l'image appropriée pour une maison en fonction de son type de consommation.
     *
     * @param m La maison pour laquelle obtenir l'image.
     * @return L'objet {@link Image} correspondant.
     */
    private Image getImageForMaison(Maison m) {
        switch (m.getTypeConso()) {
            case FORTE:
                return maisonForteImage;
            case NORMAL:
                return maisonNormaleImage;
            case BASSE:
            default:
                return maisonBasseImage;
        }
    }

    /**
     * Ouvre une boîte de dialogue pour charger une configuration de réseau depuis un fichier.
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
                reseau = new Reseau();
                reseau.chargerReseauDepuisFichier(file.getAbsolutePath());
                updateUI();
                statusLabel.setText("Réseau chargé depuis " + file.getName());
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur de chargement", "Impossible de charger le fichier : \n" + e.getMessage());
            }
        }
    }

    /**
     * Ouvre une boîte de dialogue pour sauvegarder la configuration actuelle du réseau dans un fichier.
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
                showAlert(Alert.AlertType.ERROR, "Erreur de sauvegarde", "Impossible de sauvegarder le fichier : \n" + e.getMessage());
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
