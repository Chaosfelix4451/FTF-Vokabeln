# Projektbeschreibung

**FTF-Vokabeln** ist ein Vokabeltrainer basierend auf JavaFX. Das Programm entstand im Rahmen eines Schulprojekts und erlaubt das Üben und Abfragen englischer und deutscher Begriffe. Diese Dokumentation gibt einen Überblick über Aufbau und Funktionsweise des Projekts.

## Inhaltsverzeichnis

1. [Überblick](#überblick)
2. [Projektstruktur](#projektstruktur)
3. [Build und Ausführung](#build-und-ausführung)
4. [Oberflächen und Controller](#oberflächen-und-controller)
5. [Benutzersystem](#benutzersystem)
6. [Training](#training)
7. [ScoreBoard](#scoreboard)
8. [Einstellungen](#einstellungen)


## Überblick

Das Programm bietet ein Hauptmenü mit Zugriff auf Training, Einstellungen, Benutzerverwaltung und Highscore-Anzeige. Vokabeln werden aus einer fest hinterlegten Liste im `TrainerModel` geladen. Für jeden Benutzer merkt sich das Programm den Punktestand sowie detaillierte Statistiken, die in einer CSV-Datei gespeichert werden.

## Projektstruktur

```
FTF-Vokabeln/
├── build.xml            Ant-Buildskript
├── docs/                Dokumentation
├── src/                 Quellcode und Ressourcen
│   ├── Main.java        Einstiegspunkt
│   ├── MainMenu/        Hauptmenü
│   ├── Trainer/         Logik und Oberfläche des Trainings
│   ├── ScoreBoard/      Highscore-Anzeige
│   ├── Settings/        Einstellungen
│   ├── UserManagement/  Benutzerverwaltung
│   └── Utils/           Hilfsklassen
```

Die CSS- und FXML-Dateien liegen jeweils im gleichen Unterordner wie die zugehörigen Controller.

## Build und Ausführung

Vorausgesetzt werden JDK 11, JavaFX und Ant. Das Projekt kann über das beiliegende `build.xml` kompiliert werden:

```bash
sudo apt-get update && sudo apt-get install -y ant openjdk-11-jdk openjfx
ant jar
java -jar build/jar/FTF-Vokabeln.jar
```

Alternativ lässt sich der Code in jeder IDE mit JavaFX-Unterstützung starten. `Main.java` enthält den Einstiegspunkt.

## Oberflächen und Controller

### SceneLoader und StageAwareController

Alle Szenen werden über die Hilfsklasse `SceneLoader` geladen. Sie setzt die Stage, lädt die gewünschte FXML-Datei und bindet optional ein passendes CSS ein. Controller können von `StageAwareController` erben, um die Stage automatisch zu erhalten.

### MainMenuController

* Zeigt das Hauptmenü an
* Kann Training, Einstellungen, Benutzerverwaltung und ScoreBoard öffnen
* Speichert den aktuell gewählten Benutzer mittels `UserSystem`

### UserManagementController

* Listet alle vorhandenen Benutzer auf
* Neuer Benutzer kann erstellt und anschließend ausgewählt werden
* Änderungen werden in `user_data.csv` gespeichert

## Benutzersystem

Das `UserSystem` verwaltet alle Benutzer samt Punkteständen. Es nutzt ausschließlich statische Methoden und speichert die Daten in `src/Utils/UserScore/user_data.csv`. Zu jeder Vokabelliste wird eine Statistik geführt. Wichtige Funktionen:

* `addUser`, `removeUser`, `getAllUserNames`
* Punktestand über `addPoint` und `getPoints`
* Aufzeichnen von Antworten über `recordAnswer`
* Sortieren nach Punkten (`sortByScoreDescending`)
* Laden und Speichern der Daten mit `loadFromFile` und `saveToFile`

## Training

Der `TrainerController` steuert den Ablauf des Trainings:

1. Beim Start wird der aktuelle Benutzer geladen und eine neue Sitzung begonnen.
2. Abhängig vom in den Einstellungen gewählten Modus (Deutsch→Englisch, Englisch→Deutsch, Zufällig) werden Vokabeln aus dem `TrainerModel` gewählt.
3. `loadNextVocabSet` baut dynamisch Eingabefelder auf und merkt sich die korrekten Lösungen.
4. `checkAnswers` vergleicht die Eingaben mit der Lösung, färbt richtige und falsche Buchstaben ein und aktualisiert den Punktestand über `UserSystem`.
5. Nach einer kurzen Pause wird das nächste Set geladen oder das ScoreBoard geöffnet.

Soundeffekte werden über `SoundModel` abgespielt.

## ScoreBoard

`ScoreBoardController` zeigt alle Benutzer sortiert nach Punkten an und informiert über den Fortschritt der aktuellen Sitzung. Die Daten werden direkt aus dem `UserSystem` geladen.

## Einstellungen

Im `SettingsController` wählt der Nutzer den gewünschten Vokabelmodus aus. Die Auswahl wird über `java.util.prefs.Preferences` gespeichert und beim nächsten Start wieder geladen.


