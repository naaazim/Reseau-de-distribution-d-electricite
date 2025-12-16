# Projet PAA - Réseau de distribution d'électricité

**Licence 3 Informatique – Université Paris Cité**  
Parties 1 & 2

##  Auteurs

- HAMIA Abderahmane Nazim
- FERHANI Ales Amazigh
- BENOUFELLA Mohamed Yacine

**Groupe :** Jeudi Matin

---

##  Présentation

Ce projet modélise et optimise un réseau de distribution d'électricité.

### Composants du réseau

- **Générateurs** : capacité maximale en kW
- **Maisons** : catégories de consommation (BASSE, NORMAL, FORTE)
- **Connexions** : une maison connectée à un unique générateur

### Fonctionnalités

- Chargement du réseau depuis un fichier texte
- Calcul du coût (dispersion + surcharge pénalisée par λ)
- Modification et sauvegarde de configurations
- Optimisation automatique via une heuristique locale
- Interface graphique JavaFX (bonus)

---

##  Exécution

Le point d'entrée principal du programme est : `com.example.Main`  
Les fichiers compilés se trouvent dans `target/classes/`.

### Mode automatique (lecture d'un fichier + optimisation)

```bash
java -cp target/classes com.example.Main <fichier_reseau> <lambda>
```

**Exemple :**
```bash
java -cp target/classes com.example.Main reseau.txt 10
```

### Mode manuel (aucun argument)

```bash
java -cp target/classes com.example.Main
```

### Lancer l'interface graphique JavaFX

Pour exécuter la GUI, Maven doit gérer JavaFX. Depuis le répertoire racine du projet :

```bash
mvn clean javafx:run
```

**Avantages :**
- Fonctionne sur tous les environnements
- Pas besoin d'ajouter JavaFX au module-path manuellement

---

## 📄 Format du fichier réseau

**Ordre obligatoire :** générateurs → maisons → connexions

### Exemple

```prolog
generateur(gen1,60).
maison(maison1,NORMAL).
connexion(gen1,maison1).
```

### Contraintes

- Noms alphanumériques
- Points obligatoires en fin de ligne
- Une maison doit être connectée à un unique générateur
- Impossible d'utiliser un élément non défini
- L'ordre global doit être respecté

---

##  Fonctionnalités implémentées

### Partie 1

- Construction manuelle d'un réseau
- Vérification des contraintes structurelles
- Calcul du coût : **Coût = Dispersion + λ × Surcharge**
- Affichage du réseau
- Modification des connexions

### Partie 2

- Lecture et validation complète d'un fichier
- Détection et affichage des erreurs de format
- Résolution automatique (heuristique efficace)
- Sauvegarde d'une instance au format standard

### Bonus

- Interface graphique JavaFX via `mvn javafx:run`
- Tests unitaires dans `src/test/java/`

---

##  Algorithme d'optimisation

### Heuristique de type Hill Climbing

1. Calcul du coût initial
2. Test du déplacement d'une maison vers différents générateurs
3. Acceptation uniquement si le coût diminue
4. Répétition jusqu'à stabilisation (aucune amélioration possible)

### Avantages

- Convergence rapide
- Améliore efficacement une solution existante
- Plus performant que l'algorithme naïf proposé dans l'énoncé

### Limites

- Peut atteindre un optimum local
- Ne garantit pas la solution optimale globale

---

## 📁 Structure du projet

```
.
├── fichier.txt
├── pom.xml
├── readme.txt
├── sauvegarde.txt
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── example
│   │   │           ├── Main.java
│   │   │           ├── factory
│   │   │           │   ├── GenerateurFactory.java
│   │   │           │   └── MaisonFactory.java
│   │   │           ├── gui
│   │   │           │   ├── GuiMain.java
│   │   │           │   └── Launcher.java
│   │   │           └── reseau
│   │   │               ├── Generateur.java
│   │   │               ├── Maison.java
│   │   │               ├── Reseau.java
│   │   │               └── TypeConso.java
│   │   └── resources
│   │       └── com
│   │           └── example
│   │               └── gui
│   │                   ├── generateur.png
│   │                   ├── maisonBasse.png
│   │                   ├── maisonForte.png
│   │                   └── maisonNormale.png
│   └── test
│       ├── java
│       │   └── com
│       │       └── example
│       │           └── reseau
│       │               └── ReseauUtilsTest.java
│       └── resources
│           └── test-data.csv
└── target
    ├── classes
    ├── surefire-reports
    └── test-classes
```

### Classes principales

| Classe | Rôle |
|--------|------|
| `Main` | Point d'entrée du programme (CLI & optimisation) |
| `Launcher` | Lance la GUI JavaFX via Maven |
| `Reseau` | Cœur du modèle |
| `Generateur`, `Maison` | Entités du réseau |
| `Factories` | Création contrôlée des objets |

---

##  Notes

- L'heuristique peut rester bloquée en optimum local
- La GUI fonctionne uniquement via Maven : `mvn clean javafx:run`
- Les erreurs de fichier sont détectées et affichées ligne par ligne
- Amélioration future : ajouter une recherche multi-démarrage (multi-start)

---

## Technologies utilisées

- **Java** (version compatible avec JavaFX)
- **Maven** (gestion des dépendances et build)
- **JavaFX** (interface graphique)
- **JUnit** (tests unitaires)

---

## Licence

Projet réalisé dans le cadre du module :  
**Programmation avancée et application — L3 Informatique**  
Université Paris Cité
