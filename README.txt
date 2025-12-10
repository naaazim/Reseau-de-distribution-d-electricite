================================================================================
PROJET PAA - RESEAU DE DISTRIBUTION D'ELECTRICITE

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

Le problème d'affectation des maisons aux générateurs, sous contraintes de
capacités et avec une fonction de coût non linéaire, est NP-difficile.

Cela signifie que :
  - le nombre de configurations possibles augmente de manière exponentielle,
  - un algorithme exact garantissant toujours la solution optimale devient
    rapidement inutilisable dès que le nombre de maisons dépasse une vingtaine.

Nous avons étudié plusieurs alternatives :
  - Algorithmes exacts (exhaustifs)
      -> garantissent l'optimalité mais explosent en temps sur les grandes
         instances.
  - Méthodes stochastiques (algorithmes génétiques)
      -> nécessitent beaucoup de réglages et ne garantissent pas de meilleures
         performances compte tenu de la taille du problème.
  - Heuristiques gloutonnes simples
      -> rapides, mais trop limitées : elles produisent de mauvaises solutions
         sur des réseaux déséquilibrés.

Le choix final s'est porté sur une heuristique hybride, pour les raisons
suivantes :

1) Rapidité d'exécution
   L'algorithme fournit une solution en temps raisonnable, même pour des
   réseaux comportant de nombreux générateurs et maisons.

2) Amélioration réelle du coût
   L'heuristique construit une première répartition puis améliore la solution
   en testant localement des déplacements pertinents de maisons.

3) Qualité des solutions obtenues
   Dans certains cas, l'algorithme atteint effectivement la solution optimale.
   Dans d'autres, il produit une solution très proche de l'optimum, ce qui est
   acceptable dans un contexte où le calcul doit rester rapide.

4) Stabilité et simplicité d'utilisation
   L'algorithme ne dépend pas de paramètres complexes et se comporte de manière
   prévisible sur toutes les configurations testées.

Limites connues :
  - L'heuristique peut se bloquer dans un optimum local.
  - Elle ne garantit pas l'optimum global.
  - La qualité dépend de la structure du réseau.

Malgré ces limites, elle représente le meilleur compromis entre :
  - exactitude,
  - rapidité,
  - simplicité,
  - fiabilité.

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

- L'heuristique peut rester bloquée en optimum local
- Les erreurs de fichier sont détectées et affichées ligne par ligne

================================================================================