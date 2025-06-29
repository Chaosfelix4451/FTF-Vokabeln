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

Das Programm bietet ein Hauptmenü mit Zugriff auf Training, Einstellungen, Benutzerverwaltung und Highscore-Anzeige. Die Vokabellisten werden beim Start aus JSON-Dateien geladen und können dadurch leicht erweitert werden. Für jeden Benutzer merkt sich das Programm den Punktestand sowie detaillierte Statistiken, die in einer CSV-Datei gespeichert werden.

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
* Speichert den aktuell gewählten Benutzer mittels `UserSys`

### UserManagementController

* Listet alle vorhandenen Benutzer auf
* Neuer Benutzer kann erstellt und anschließend ausgewählt werden
* Änderungen werden in `user_data.csv` gespeichert

## Benutzersystem

Das `UserSys`-Modul verwaltet alle Benutzer samt Punkteständen in einer JSON-Datei. Es stellt komfortable Methoden zum Erstellen, Suchen und Löschen bereit und erlaubt das Abfragen bzw. Setzen der Punkte.

Wichtige Funktionen:

* `createUser`, `deleteUser`, `searchUsers`
* Punktestand über `addPoints`, `getScore` und `setScore`
* Laden und Speichern der Daten mit `loadFromJson` und `saveToJson`

## Training

Der `TrainerController` steuert den Ablauf des Trainings:

1. Beim Start wird der aktuelle Benutzer geladen und eine neue Sitzung begonnen.
2. Abhängig vom in den Einstellungen gewählten Modus (z.B. Deutsch→Englisch oder Zufällig) werden Vokabeln aus der gewählten JSON-Liste im `TrainerModel` geladen.
3. `loadNextVocabSet` baut dynamisch Eingabefelder auf und merkt sich die korrekten Lösungen.
4. `checkAnswers` vergleicht die Eingaben mit der Lösung, färbt richtige und falsche Buchstaben ein und aktualisiert den Punktestand über `UserSys`.
5. Nach einer kurzen Pause wird das nächste Set geladen oder das ScoreBoard geöffnet.

Soundeffekte werden über `SoundModel` abgespielt.

## ScoreBoard

`ScoreBoardController` zeigt alle Benutzer sortiert nach Punkten an und informiert über den Fortschritt der aktuellen Sitzung. Die Daten werden direkt aus dem `UserSys` geladen.

## Einstellungen

Im `SettingsController` wählt der Nutzer den gewünschten Vokabelmodus sowie die zu verwendende Vokabelliste aus. Diese Einstellungen werden jetzt gemeinsam mit dem aktuellen Dark‑Mode‑Status in `user.json` gespeichert und beim nächsten Start wieder geladen. Die verfügbaren Modi richten sich nach den Sprachen der gewählten Liste. Neue JSON-Dateien im Ordner `src/Trainer/Vocabsets` erscheinen automatisch in der Auswahlliste.


