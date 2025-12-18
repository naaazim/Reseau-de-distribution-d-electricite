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
capacités et avec une fonction de coût combinant équilibre des charges et
pénalisation des surcharges, est un problème NP-difficile.

Le nombre de configurations possibles croît de manière exponentielle avec le
nombre de maisons et de générateurs. Une recherche exhaustive devient donc
rapidement irréaliste, même pour des tailles de réseaux modérées.

Choix de l'algorithme :

Afin de garantir des temps de calcul raisonnables tout en produisant des
configurations de bonne qualité, nous avons choisi une approche heuristique
plutôt qu'un algorithme exact.

L'algorithme implémenté combine :
  - une phase gloutonne guidée par la fonction de coût,
  - suivie d'une phase de recherche locale par améliorations successives.

Ce choix permet d'obtenir un excellent compromis entre performance,
qualité des solutions et simplicité de mise en œuvre.

Principe de fonctionnement :

L'algorithme d'optimisation fonctionne en trois phases principales :

1. Prétraitement et tri :
   - Les générateurs sont triés par capacité décroissante.
   - Les maisons sont triées par consommation décroissante.
   Ce tri permet de traiter en priorité les éléments les plus contraignants.

2. Affectation gloutonne initiale :
   - Chaque maison est affectée au générateur qui minimise le coût global
     du réseau après affectation.
   - L'algorithme privilégie en priorité les générateurs capables d'accueillir
     la maison sans dépasser leur capacité.
   - Si aucune affectation sans surcharge n'est possible, une surcharge
     contrôlée est autorisée afin de garantir une solution complète.

3. Amélioration par recherche locale :
   - Une phase d'optimisation locale est ensuite appliquée.
   - Pour chaque maison, l'algorithme teste son déplacement vers d'autres
     générateurs.
   - Un déplacement est conservé uniquement s'il réduit le coût total
     (dispersion + surcharge pondérée).
   - Cette phase est répétée jusqu'à stabilisation, c'est-à-dire lorsqu'aucune
     amélioration supplémentaire n'est possible.

Fonction de coût :

La qualité d'une configuration est évaluée à l'aide de la fonction de coût :

  Coût = Dispersion + lambda × Surcharge

où :
  - la dispersion mesure l'équilibre des taux d'utilisation des générateurs,
  - la surcharge pénalise les dépassements de capacité,
  - lambda est un paramètre réglable contrôlant l'importance de la surcharge.

Garanties et propriétés :

  - L'algorithme garantit toujours une solution valide dans laquelle
    toutes les maisons sont affectées à un générateur.
  - Le coût du réseau ne peut qu'être amélioré ou rester stable au cours
    de la phase de recherche locale.
  - Le résultat est déterministe : une même instance et une même valeur
    de lambda produisent toujours la même configuration finale.

Performances et limites :

  - L'algorithme ne garantit pas l'optimalité globale, le problème étant
    NP-difficile.
  - En contrepartie, les temps de calcul restent faibles, même pour des
    réseaux de taille significative.
  - En pratique, les solutions obtenues sont très proches de l'optimum
    et nettement meilleures que les configurations initiales.

Résumé :

Ce choix algorithmique permet d'obtenir :
  - des temps d'exécution maîtrisés,
  - une implémentation claire et robuste,
  - une qualité de solution élevée,
  - une adaptation naturelle aux contraintes du problème réel.

Il constitue un compromis efficace entre optimisation et faisabilité
dans le cadre du projet PAA.


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

- La résolution automatique du réseau repose sur une approche heuristique
  adaptée à un problème NP-difficile.

- L’algorithme garantit une configuration valide : toutes les maisons
  sont connectées à un générateur.

- Le coût du réseau combine l’équilibrage des charges et la pénalisation
  des surcharges via le paramètre λ.

- Les erreurs de fichiers et les incohérences sont détectées et signalées.

================================================================================
