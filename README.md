<p align="center">
  <img src="src/Utils/media/Logo.png" alt="Projektlogo" width="250">
</p>

[![Java CI](https://github.com/Chaosfelix4451/FTF-Vokabeln/actions/workflows/ant.yml/badge.svg)](https://github.com/Chaosfelix4451/FTF-Vokabeln/actions/workflows/ant.yml)

FTF-Vokabeln ist ein kleiner Vokabeltrainer auf Basis von JavaFX. Das Projekt entstand im Rahmen eines Schulprojekts und hilft beim Üben englischer Wörter.

## Funktionen

- zufällige Abfragen in beide Richtungen
- Nutzerverwaltung mit gespeichertem Highscore
- Anzeige des Scoreboards
- Speichern der Einstellungen
- einfache Soundeffekte
- zentraler `UserSys`-Verwaltungscode mit einfacher Nutzerverwaltung
- Vokabellisten als JSON-Dateien, leicht erweiterbar

## Build

Vorausgesetzt werden JDK 11, JavaFX und Ant. Unter Ubuntu können die Pakete wie folgt installiert und das JAR gebaut werden:

```bash
sudo apt-get update && sudo apt-get install -y ant openjdk-11-jdk openjfx
ant jar
java -jar build/jar/FTF-Vokabeln.jar
```

## Vokabellisten hinzufügen

Alle Listen befinden sich im Ordner `src/Trainer/Vocabsets`. Beim Start wird die
in den Einstellungen gewählte Datei geladen. Neue Listen können einfach als
JSON-Dateien mit folgendem Aufbau hinzugefügt werden:

```json
[
  {
    "translations": {
      "en": "computer",
      "de": "Computer"
    }
  }
]
```

Der Dateiname erscheint anschließend in den Einstellungen zur Auswahl.

## Dokumentation

Ausführliche Erläuterungen zu Aufbau und Funktionsweise bietet die interaktive Dokumentation unter [`docs/index.html`](docs/index.html). Eine kurze Übersicht der wichtigsten Methoden steht in `docs/Methodenliste.md`.

## Mitwirken

Fehlerberichte und Pull Requests sind willkommen.

## Autoren

- Felix
- Toby
- Feras

## Lizenz

Dieses Projekt dient zu Lernzwecken und besitzt keine explizite Lizenzdatei.
