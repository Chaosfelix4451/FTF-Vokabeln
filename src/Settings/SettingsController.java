package Settings;


import Utils.Confetti.Confetti;
import Utils.SceneLoader.SceneLoader;
import Utils.StageAwareController;
import Utils.StageRegistry;
import Utils.UserSys.UserSys;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller für die Einstellungen.
 */
public class SettingsController extends StageAwareController implements Initializable {

    public Label mainLable;
    public Button Button;
    public CheckBox darkModeToggle;
    public Label vDark;
    public Button confettibutton;


    /**
     * Schließt das Einstellungsfenster und kehrt zum Hauptmenü zurück.
     */
    @FXML
    public void openMainMenu(ActionEvent event) {
        this.stage.close();
    }

    @FXML
    private ChoiceBox<String> vocabModeBox;
    @FXML
    private ChoiceBox<String> vocabListBox;

    private static final java.util.Map<String, String> LANG_NAMES = java.util.Map.of(
            "de", "Deutsch",
            "en", "Englisch",
            "fr", "Französisch",
            "es", "Spanisch"
    );

    /**
     * Erstellt eine Liste möglicher Sprachmodi aus den vorhandenen Sprachen.
     */
    private java.util.List<String> generateModes(java.util.Set<String> langs) {
        java.util.List<String> codes = new java.util.ArrayList<>(LANG_NAMES.keySet());
        codes.removeIf(code -> !langs.contains(code));
        for (String code : langs) {
            if (!codes.contains(code)) codes.add(code);
        }

        java.util.List<String> modes = new java.util.ArrayList<>();
        for (String q : codes) {
            for (String a : codes) {
                if (!q.equals(a)) {
                    String qName = LANG_NAMES.getOrDefault(q, q);
                    String aName = LANG_NAMES.getOrDefault(a, a);
                    modes.add(qName + " zu " + aName);
                }
            }
        }
        if (codes.size() >= 2) {
            modes.add("Zufällig");
        }
        return modes;
    }

    /**
     * Aktualisiert die Auswahl der Sprachmodi anhand der gewählten Vokabelliste.
     */
    private void updateVocabModes() {
        String file = vocabListBox.getValue();
        if (file == null) return;
        Trainer.TrainerModel model = new Trainer.TrainerModel();
        model.LoadJSONtoDataObj(file);
        java.util.Set<String> langs = model.getAvailableLanguages();
        java.util.List<String> options = generateModes(langs);
        String current = vocabModeBox.getValue();
        vocabModeBox.getItems().setAll(options);
        if (current != null && options.contains(current)) {
            vocabModeBox.setValue(current);
        } else if (!options.isEmpty()) {
            if (options.contains("Zufällig")) {
                vocabModeBox.setValue("Zufällig");
            } else {
                vocabModeBox.setValue(options.get(0));
            }
        }
    }

    /**
     * Initialisiert die ComboBox für den Vokabelmodus und lädt gespeicherte Werte.
     */

    /**
     * Lädt gespeicherte Einstellungen und bereitet die Auswahlfelder sowie den
     * Dark‑Mode‑Schalter vor.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("SettingsController: initialize");
        UserSys.loadFromJson();

        for (String name : listVocabFiles()) {
            vocabListBox.getItems().add(name);
        }

        String savedFile = UserSys.getPreference("vocabFile", "defaultvocab.json");
        if (vocabListBox.getItems().contains(savedFile)) {
            vocabListBox.setValue(savedFile);
        }

        updateVocabModes();

        String savedMode = UserSys.getPreference("vocabMode", "Zufällig");
        if (vocabModeBox.getItems().contains(savedMode)) {
            vocabModeBox.setValue(savedMode);
        }

        vocabModeBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                UserSys.setPreference("vocabMode", newVal);
                UserSys.saveToJson();
            }
        });

        vocabListBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                UserSys.setPreference("vocabFile", newVal);

            }
            updateVocabModes();
            String mode = vocabModeBox.getValue();
            if (mode != null) {
                UserSys.setPreference("vocabMode", mode);
            }
            UserSys.saveToJson();
        });

        boolean dark = UserSys.getBooleanPreference("darkMode", false);
        darkModeToggle.setSelected(dark);
        darkModeToggle.selectedProperty().addListener((obs, o, n) -> {
            UserSys.setBooleanPreference("darkMode", n);
            updateDarkThemeState();
            UserSys.saveToJson();
        });

        javafx.application.Platform.runLater(this::updateDarkThemeState);

    }

    /**
     * Beispielhafter Handler für den Startknopf. Hier könnte später der
     * ausgewählte Trainingsmodus gestartet werden.
     */
    @FXML
    private void handleStart(ActionEvent event) {
        String auswahl = vocabModeBox.getValue();

        switch (auswahl) {
            case "Deutsch zu Englisch":
                // Starte Deutsch->Englisch Modus
                break;
            case "Englisch zu Deutsch":
                // Starte Englisch->Deutsch Modus
                break;
            case "Zufällig":
                // Wähle zufällig einen Modus
                break;
        }
    }

    /**
     * Durchsucht den lokalen Ordner nach Vokabellisten und ergänzt
     * eingebettete Ressourcen.
     */
    private java.util.List<String> listVocabFiles() {
        java.io.File vocabDir = new java.io.File("src/Trainer/Vocabsets");
        java.io.File[] files = vocabDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));
        java.util.List<String> result = new java.util.ArrayList<>();
        if (files != null && files.length > 0) {
            for (java.io.File f : files) {
                result.add(f.getName());
            }
            return result;
        }
        result.addAll(listResourceFiles("/Trainer/Vocabsets"));
        return result;
    }

    /**
     * Listet alle Ressourcen-Dateien eines Pfades innerhalb des JAR auf.
     */
    private java.util.List<String> listResourceFiles(String path) {
        java.util.List<String> result = new java.util.ArrayList<>();
        try {
            java.net.URI uri = getClass().getResource(path).toURI();
            java.nio.file.Path dirPath;
            if ("jar".equals(uri.getScheme())) {
                java.nio.file.FileSystem fs;
                try {
                    fs = java.nio.file.FileSystems.getFileSystem(uri);
                } catch (java.nio.file.FileSystemNotFoundException e) {
                    fs = java.nio.file.FileSystems.newFileSystem(uri, java.util.Collections.emptyMap());
                }
                dirPath = fs.getPath(path);
            } else {
                dirPath = java.nio.file.Paths.get(uri);
            }
            try (java.nio.file.DirectoryStream<java.nio.file.Path> stream = java.nio.file.Files.newDirectoryStream(dirPath, "*.json")) {
                for (java.nio.file.Path p : stream) {
                    result.add(p.getFileName().toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Aktiviert oder deaktiviert das dunkle Design für alle Fenster.
     */
    private void updateDarkThemeState() {
        boolean enableDarkMode = darkModeToggle.isSelected();
        Utils.StageRegistry.applyDarkMode(enableDarkMode);
    }

    /**
     * Zeigt zur Demonstration Konfetti im aktuellen Fenster.
     */
    @FXML
    private void adminConfetti() {
        Confetti.show((Pane) confettibutton.getScene().getRoot());
    }

    /**
     * Öffnet direkt den ConnectTrainer aus den Einstellungen heraus.
     */
    @FXML
    public void openConnectTrainer() {
        SceneLoader.load("/ConnectTrainer/ConnectTrainer.fxml");
    }


}
