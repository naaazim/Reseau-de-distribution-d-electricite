================================================================================
PROJET PAA - RESEAU DE DISTRIBUTION D'ELECTRICITE

Licence 3 Informatique - Universite Paris Cite
Parties 1 & 2

Auteurs :
  - HAMIA Abderahmane Nazim
  - FERHANI Ales Amazigh
  - BENOUFELLA Mohamed Yacine

Groupe : Jeudi Matin

================================================================================
PRESENTATION
================================================================================

Ce projet modelise et optimise un reseau de distribution d'electricite.

Composants du reseau :
  - Generateurs : capacite maximale en kW
  - Maisons : categories de consommation (BASSE, NORMAL, FORTE)
  - Connexions : une maison connectee a un unique generateur

Fonctionnalites :
  - Chargement du reseau depuis un fichier texte
  - Calcul du cout (dispersion + surcharge penalisee par lambda)
  - Modification et sauvegarde de configurations
  - Optimisation automatique via une heuristique locale
  - Interface graphique (bonus) lancee via Maven

================================================================================
EXECUTION
================================================================================

Le point d'entree principal du programme est : com.example.Main
Les fichiers compiles se trouvent dans target/classes/.

Mode automatique (lecture d'un fichier + optimisation)
-------------------------------------------------------
java -cp target/classes com.example.Main <fichier_reseau> <lambda>

Exemple :
  java -cp target/classes com.example.Main reseau.txt 10

Mode manuel (aucun argument)
----------------------------
java -cp target/classes com.example.Main

Lancer l'interface graphique JavaFX (methode universelle)
----------------------------------------------------------
Pour executer la GUI sur n'importe quel IDE ou terminal, Maven doit gerer 
JavaFX. Depuis le repertoire racine du projet, executer simplement :

mvn clean javafx:run

Avantages :
  - Fonctionne sur tous les environnements
  - Pas besoin d'ajouter JavaFX au module-path manuellement

================================================================================
FORMAT DU FICHIER RESEAU
================================================================================

Ordre obligatoire : generateurs -> maisons -> connexions

Exemple :
  generateur(gen1,60).
  maison(maison1,NORMAL).
  connexion(gen1,maison1).

Contraintes :
  - Noms alphanumeriques
  - Points obligatoires en fin de ligne
  - Une maison doit etre connectee a un unique generateur
  - Impossible d'utiliser un element non defini
  - L'ordre global doit etre respecte

================================================================================
FONCTIONNALITES IMPLEMENTEES
================================================================================

Partie 1 :
  - Construction manuelle d'un reseau
  - Verification des contraintes structurelles
  - Calcul du cout : Cout = Dispersion + lambda * Surcharge
  - Affichage du reseau
  - Modification des connexions

Partie 2 :
  - Lecture et validation complete d'un fichier
  - Detection et affichage des erreurs de format
  - Resolution automatique (heuristique efficace)
  - Sauvegarde d'une instance au format standard

Bonus :
  - Interface graphique JavaFX via mvn javafx:run
  - Tests unitaires dans src/test/java/

================================================================================
ALGORITHME D'OPTIMISATION
================================================================================

Heuristique de type Hill Climbing :
  1. Calcul du cout initial
  2. Test du deplacement d'une maison vers differents generateurs
  3. Acceptation uniquement si le cout diminue
  4. Repetition jusqu'a stabilisation (aucune amelioration possible)

Avantages :
  - Convergence rapide
  - Ameliore efficacement une solution existante
  - Plus performant que l'algorithme naif propose dans l'enonce

Limites :
  - Peut atteindre un optimum local
  - Ne garantit pas la solution optimale globale

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
  - Main : point d'entree du programme (CLI & optimisation)
  - Launcher : lance la GUI JavaFX via Maven
  - Reseau : coeur du modele
  - Generateur, Maison : entites du reseau
  - Factories : creation controlee des objets

================================================================================
NOTES
================================================================================

- L'heuristique peut rester bloquee en optimum local
- Les erreurs de fichier sont detectees et affichees ligne par ligne

================================================================================