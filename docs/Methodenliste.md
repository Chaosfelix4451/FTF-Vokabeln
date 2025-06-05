# Methodenübersicht

Dieser Anhang listet exemplarisch einige wichtige Methoden des Projekts auf.

## MainMenuController
- `openTrainer(ActionEvent)` – startet den Trainer.
- `openSettings(ActionEvent)` – öffnet die Einstellungen.
- `openUserManagement(ActionEvent)` – öffnet die Benutzerverwaltung.
- `openScoreBoard(ActionEvent)` – zeigt die Highscore-Tabelle.

## SettingsController
- `openMainMenu(ActionEvent)` – zurück zum Hauptmenü.
- `openTrainer(ActionEvent)` – startet den Trainer.
- `initialize(URL, ResourceBundle)` – initialisiert die Auswahl des Vokabelmodus.

## TrainerController
- `initialize()` – lädt erste Vokabeln und setzt Benutzer.
- `loadNextVocabSet()` – erstellt neue Eingabefelder.
- `checkAnswers()` – wertet Antworten aus und zeigt das Ergebnis.

## UserManagementController
- `createUser()` – legt einen neuen Benutzer an.
- `selectUser()` – wählt einen Benutzer aus der Liste aus.
- `refreshList(String)` – filtert die Benutzerliste.

## UserSystem (statisch)
- `loadFromFile()` – lädt gespeicherte Benutzerdaten.
- `saveToFile()` – speichert alle Daten.
- `addPoint(String)` – erhöht den Punktestand eines Nutzers.


Weitere Methoden sind in den jeweiligen Klassen dokumentiert.

