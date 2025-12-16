================================================================================
PROJET PAA - RESEAU DE DISTRIBUTION D'ELECTRICITE
================================================================================

Licence 3 Informatique - Université Paris Cité
Parties 1 & 2

Auteurs :
  - HAMIA Abderahmane Nazim
  - FERHANI Ales Amazigh
  - BENOUFELLA Mohamed Yacine

Groupe : Jeudi Matin

================================================================================
PRESENTATION
================================================================================

Ce projet modélise et optimise un réseau de distribution d'électricité.

Composants du réseau :
  - Générateurs : capacité maximale en kW
  - Maisons : catégories de consommation (BASSE, NORMAL, FORTE)
  - Connexions : une maison connectée à un unique générateur

Fonctionnalités :
  - Chargement du réseau depuis un fichier texte
  - Calcul du coût (dispersion + surcharge pénalisée par lambda)
  - Modification et sauvegarde de configurations
  - Optimisation automatique via une heuristique locale
  - Interface graphique (bonus) lancée via Maven

================================================================================
EXECUTION
================================================================================

Le point d'entrée principal du programme est : com.example.Main
Les fichiers compilés se trouvent dans target/classes/.

Mode automatique (lecture d'un fichier + optimisation)
-------------------------------------------------------
java -cp target/classes com.example.Main <fichier_reseau> <lambda>

Exemple :
  java -cp target/classes com.example.Main fichier.txt 10

Mode manuel (aucun argument)
----------------------------
java -cp target/classes com.example.Main

Lancer l'interface graphique JavaFX (méthode universelle)
----------------------------------------------------------
Pour exécuter la GUI sur n'importe quel IDE ou terminal, Maven doit gérer
JavaFX. Depuis le répertoire racine du projet, exécuter simplement :

mvn clean javafx:run

Avantages :
  - Fonctionne sur tous les environnements
  - Pas besoin d'ajouter JavaFX au module-path manuellement

================================================================================
FORMAT DU FICHIER RESEAU
================================================================================

Ordre obligatoire : générateurs -> maisons -> connexions

Exemple :
  generateur(gen1,60).
  maison(maison1,NORMAL).
  connexion(gen1,maison1).

Contraintes :
  - Noms alphanumériques
  - Points obligatoires en fin de ligne
  - Une maison doit être connectée à un unique générateur
  - Impossible d'utiliser un élément non défini
  - L'ordre global doit être respecté

================================================================================
FONCTIONNALITES IMPLEMENTEES
================================================================================

Partie 1 :
  - Construction manuelle d'un réseau
  - Vérification des contraintes structurelles
  - Calcul du coût : Coût = Dispersion + lambda * Surcharge
  - Affichage du réseau
  - Modification des connexions

Partie 2 :
  - Lecture et validation complète d'un fichier
  - Détection et affichage des erreurs de format
  - Résolution automatique (heuristique efficace)
  - Sauvegarde d'une instance au format standard

Bonus :
  - Interface graphique JavaFX via mvn javafx:run
  - Tests unitaires dans src/test/java/

================================================================================
ALGORITHME D'OPTIMISATION : JUSTIFICATION DU CHOIX
================================================================================

Nature du problème :

Le problème d'affectation des maisons aux générateurs, sous contraintes de
capacités et avec une fonction de coût combinant équilibre et surcharge, est un
problème NP-difficile.

Le nombre de configurations possibles croît de manière exponentielle avec le
nombre de maisons et de générateurs, ce qui rend une exploration exhaustive
naïve rapidement impossible.

Choix de l'algorithme

Pour cette raison, nous avons implémenté un algorithme exact de type
Branch & Bound, qui permet de :

  - garantir la solution optimale pour l'instance considérée ;
  - réduire drastiquement l'espace de recherche grâce à l'élagage ;
  - rester exploitable sur des réseaux de taille raisonnable.

Principe de fonctionnement

L'algorithme procède de la manière suivante :

  1. Toutes les maisons (connectées ou non) sont collectées.

  2. Les maisons sont triées par consommation décroissante afin de traiter
     en priorité les affectations les plus contraignantes.

  3. Les générateurs sont triés par capacité décroissante.

  4. Une exploration récursive teste les affectations possibles maison par
     maison.

  5. À chaque étape, une borne inférieure du coût est évaluée :
     - si cette borne est supérieure au meilleur coût connu, la branche est
       abandonnée.

  6. Lorsqu'une affectation complète est trouvée avec un coût inférieur au
     meilleur connu, elle devient la nouvelle solution optimale.

Garanties apportées

  - L'algorithme explore l'espace des solutions de manière complète, sous
    réserve de l'élagage.
  - La meilleure solution retournée est optimalement minimale selon la
    fonction de coût définie.
  - Le résultat est déterministe : une même instance produit toujours la
    même solution optimale.

Performances et limites

  - Le pire cas reste exponentiel (car le problème est NP-difficile).
  - Grâce au tri heuristique et à l'élagage, l'algorithme est très efficace
    en pratique pour les tailles de réseaux attendues dans le cadre du projet.
  - L'algorithme est particulièrement adapté aux réseaux de taille petite à
    moyenne, où l'optimalité est recherchée.

Résumé

Ce choix permet d'obtenir un excellent compromis entre :
  - Exactitude (solution optimale garantie),
  - Performance (élagage efficace),
  - Lisibilité du code,
  - Robustesse face aux configurations complexes.

================================================================================
STRUCTURE COMPLETE DU PROJET
================================================================================

.
|-- fichier.txt
|-- out
|   |-- production
|       |-- ProjetPAA
|-- pom.xml
|-- readme.txt
|-- sauvegarde.txt
|-- src
|   |-- main
|   |   |-- java
|   |   |   |-- com
|   |   |       |-- com.iml
|   |   |       |-- example
|   |   |           |-- Main.java
|   |   |           |-- factory
|   |   |           |   |-- GenerateurFactory.java
|   |   |           |   |-- MaisonFactory.java
|   |   |           |-- gui
|   |   |           |   |-- GuiMain.java
|   |   |           |   |-- Launcher.java
|   |   |           |-- reseau
|   |   |               |-- Generateur.java
|   |   |               |-- Maison.java
|   |   |               |-- Reseau.java
|   |   |               |-- TypeConso.java
|   |   |-- resources
|   |       |-- com
|   |           |-- example
|   |               |-- gui
|   |                   |-- generateur.png
|   |                   |-- maisonBasse.png
|   |                   |-- maisonForte.png
|   |                   |-- maisonNormale.png
|   |-- test
|       |-- java
|       |   |-- com
|       |       |-- example
|       |           |-- reseau
|       |               |-- ReseauUtilsTest.java
|       |-- resources
|           |-- test-data.csv
|-- target
    |-- classes
    |   |-- com
    |       |-- example
    |           |-- Main.class
    |           |-- factory
    |           |-- gui
    |           |-- reseau
    |-- surefire-reports
    |-- test-classes

Classes principales :
  - Main : point d'entrée du programme (CLI & optimisation)
  - Launcher : lance la GUI JavaFX via Maven
  - Reseau : cœur du modèle
  - Generateur, Maison : entités du réseau
  - Factories : création contrôlée des objets

================================================================================
NOTES
================================================================================

- La résolution automatique repose sur un algorithme exact de type
  Branch & Bound.
- Toutes les contraintes du problème sont strictement respectées.
- Les erreurs de fichiers sont détectées et signalées avec précision.

================================================================================