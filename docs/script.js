document.addEventListener('DOMContentLoaded', () => {
    marked.setOptions({ headerIds: true });
    const markdown = `
# Projektbeschreibung

**FTF-Vokabeln** ist ein Vokabeltrainer basierend auf JavaFX. Das Programm entstand im Rahmen eines Schulprojekts und erlaubt das Üben und Abfragen englischer und deutscher Begriffe. Diese Dokumentation gibt einen Überblick über Aufbau und Funktionsweise des Projekts.

## Inhaltsverzeichnis

1. [Überblick](#überblick)
2. [Projektstruktur](#projektstruktur)
3. [Build und Ausführung](#build-und-ausf\u00fchrung)
4. [Oberfl\u00e4chen und Controller](#oberfl\u00e4chen-und-controller)
5. [Benutzersystem](#benutzersystem)
6. [Training](#training)
7. [ScoreBoard](#scoreboard)
8. [Einstellungen](#einstellungen)

## \u00dcberblick

Das Programm bietet ein Hauptmen\u00fc mit Zugriff auf Training, Einstellungen, Benutzerverwaltung und Highscore-Anzeige. Vokabeln werden aus einer fest hinterlegten Liste im \`TrainerModel\` geladen. F\u00fcr jeden Benutzer merkt sich das Programm den Punktestand sowie detaillierte Statistiken, die in einer CSV-Datei gespeichert werden.

## Projektstruktur

```
FTF-Vokabeln/
├── build.xml            Ant-Buildskript
├── docs/                Dokumentation
├── src/                 Quellcode und Ressourcen
│   ├── Main.java        Einstiegspunkt
│   ├── MainMenu/        Hauptmen\u00fc
│   ├── Trainer/         Logik und Oberfl\u00e4che des Trainings
│   ├── ScoreBoard/      Highscore-Anzeige
│   ├── Settings/        Einstellungen
│   ├── UserManagement/  Benutzerverwaltung
│   └── Utils/           Hilfsklassen
```

Die CSS- und FXML-Dateien liegen jeweils im gleichen Unterordner wie die zugeh\u00f6rigen Controller.

## Build und Ausf\u00fchrung

Vorausgesetzt werden JDK\u00a011, JavaFX und Ant. Das Projekt kann \u00fcber das beiliegende \`build.xml\` kompiliert werden:

```bash
sudo apt-get update && sudo apt-get install -y ant openjdk-11-jdk openjfx
ant jar
java -jar build/jar/FTF-Vokabeln.jar
```

Alternativ l\u00e4sst sich der Code in jeder IDE mit JavaFX-Unterst\u00fctzung starten. \`Main.java\` enth\u00e4lt den Einstiegspunkt.

## Oberfl\u00e4chen und Controller

### SceneLoader und StageAwareController

Alle Szenen werden \u00fcber die Hilfsklasse \`SceneLoader\` geladen. Sie setzt die Stage, l\u00e4dt die gew\u00fcnschte FXML-Datei und bindet optional ein passendes CSS ein. Controller k\u00f6nnen von \`StageAwareController\` erben, um die Stage automatisch zu erhalten.

### MainMenuController

* Zeigt das Hauptmen\u00fc an
* Kann Training, Einstellungen, Benutzerverwaltung und ScoreBoard \u00f6ffnen
* Speichert den aktuell gew\u00e4hlten Benutzer mittels \`UserSystem\`

### UserManagementController

* Listet alle vorhandenen Benutzer auf
* Neuer Benutzer kann erstellt und anschlie\u00dfend ausgew\u00e4hlt werden
* \u00c4nderungen werden in \`user_data.csv\` gespeichert

## Benutzersystem

Das \`UserSystem\` verwaltet alle Benutzer samt Punktest\u00e4nden. Es nutzt ausschlie\u00dflich statische Methoden und speichert die Daten in \`src/Utils/UserScore/user_data.csv\`. Zu jeder Vokabelliste wird eine Statistik gef\u00fchrt. Wichtige Funktionen:

* \`addUser\`, \`removeUser\`, \`getAllUserNames\`
* Punktestand \u00fcber \`addPoint\` und \`getPoints\`
* Aufzeichnen von Antworten \u00fcber \`recordAnswer\`
* Sortieren nach Punkten (\`sortByScoreDescending\`)
* Laden und Speichern der Daten mit \`loadFromFile\` und \`saveToFile\`

## Training

Der \`TrainerController\` steuert den Ablauf des Trainings:

1. Beim Start wird der aktuelle Benutzer geladen und eine neue Sitzung begonnen.
2. Abh\u00e4ngig vom in den Einstellungen gew\u00e4hlten Modus (Deutsch\u2192Englisch, Englisch\u2192Deutsch, Zuf\u00e4llig) werden Vokabeln aus dem \`TrainerModel\` gew\u00e4hlt.
3. \`loadNextVocabSet\` baut dynamisch Eingabefelder auf und merkt sich die korrekten L\u00f6sungen.
4. \`checkAnswers\` vergleicht die Eingaben mit der L\u00f6sung, f\u00e4rbt richtige und falsche Buchstaben ein und aktualisiert den Punktestand \u00fcber \`UserSystem\`.
5. Nach einer kurzen Pause wird das n\u00e4chste Set geladen oder das ScoreBoard ge\u00f6ffnet.

Soundeffekte werden \u00fcber \`SoundModel\` abgespielt.

## ScoreBoard

\`ScoreBoardController\` zeigt alle Benutzer sortiert nach Punkten an und informiert \u00fcber den Fortschritt der aktuellen Sitzung. Die Daten werden direkt aus dem \`UserSystem\` geladen.

## Einstellungen

Im \`SettingsController\` w\u00e4hlt der Nutzer den gew\u00fcnschten Vokabelmodus aus. Die Auswahl wird \u00fcber \`java.util.prefs.Preferences\` gespeichert und beim n\u00e4chsten Start wieder geladen.
`;
    const html = marked.parse(markdown);
    document.getElementById('content').innerHTML = html;
    buildTree();

    const searchInput = document.getElementById('search');
    searchInput.addEventListener('input', () => {
        const q = searchInput.value.toLowerCase();
        document.querySelectorAll('#content p, #content li').forEach(el => {
            const text = el.textContent.toLowerCase();
            if (q && text.includes(q)) el.classList.add('highlight');
            else el.classList.remove('highlight');
        });
    });
});

function buildTree() {
    const toc = document.getElementById('toc');
    const stack = [{ level: 0, ul: toc }];
    document.querySelectorAll('#content h1, #content h2, #content h3').forEach(h => {
        const level = parseInt(h.tagName.substring(1));
        const li = document.createElement('li');
        const a = document.createElement('a');
        a.textContent = h.textContent;
        a.href = '#' + h.id;
        li.appendChild(a);
        while (stack.length > level) stack.pop();
        let parent = stack[stack.length - 1];
        if (!parent.ul) {
            parent.ul = document.createElement('ul');
            parent.li.appendChild(parent.ul);
        }
        parent.ul.appendChild(li);
        stack.push({ level: level, li: li });
    });
    document.querySelectorAll('#toc li').forEach(li => {
        if (li.querySelector('ul')) {
            li.classList.add('collapsed');
            li.addEventListener('click', e => {
                if (e.target.tagName !== 'A') {
                    li.classList.toggle('collapsed');
                }
            });
        }
    });
}
