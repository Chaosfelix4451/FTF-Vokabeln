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
- `initialize()` – lädt die gewählte Vokabelliste und setzt Benutzer.
- `loadNextVocabSet()` – erstellt neue Eingabefelder.
- `checkAnswers()` – wertet Antworten aus und zeigt das Ergebnis.

## TrainerModel
- `loadFromJson(String)` – liest Vokabeln aus einer JSON-Datei ein.

## UserManagementController
- `createUser()` – legt einen neuen Benutzer an.
- `selectUser()` – wählt einen Benutzer aus der Liste aus.
- `refreshList(String)` – filtert die Benutzerliste.


Weitere Methoden sind in den jeweiligen Klassen dokumentiert.

