package Settings;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import java.io.File;
import Utils.SceneLoader.SceneLoader;
import Utils.StageAwareController;
import javafx.scene.control.Label;


import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

/**
 * Controller für die Einstellungen.
 */
public class SettingsController extends StageAwareController implements Initializable {

    // Einstellungen werden im Preferences-Objekt gespeichert
    private final Preferences prefs = Preferences.userNodeForPackage(SettingsController.class);

    public Label mainLable;
    public Button Button;
    public Button button;
    public CheckBox darkModeToggle;
    public Label vDark;

    /**
     * Zurück zum Hauptmenü.
     */
    @FXML
    public void openMainMenu(ActionEvent event) {
        SceneLoader.load("/MainMenu/mainMenu.fxml");
    }

    /**
     * Startet den Trainer.
     */
    @FXML
    public void openTrainer(ActionEvent event) {
        SceneLoader.load(stage, "/Trainer/Trainer.fxml");
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

    private void updateVocabModes() {
        String file = vocabListBox.getValue();
        if (file == null) return;
        Trainer.TrainerModel model = new Trainer.TrainerModel();
        model.LoadJSONtoDataObj("src/Trainer/Vocabsets/" + file);
        java.util.Set<String> langs = model.getAvailableLanguages();
        java.util.List<String> options = generateModes(langs);
        String current = vocabModeBox.getValue();
        vocabModeBox.getItems().setAll(options);
        if (current != null && options.contains(current)) {
            vocabModeBox.setValue(current);
        } else if (!options.isEmpty()) {
            vocabModeBox.setValue(options.get(0));
        }
    }

    /**
     * Initialisiert die ComboBox für den Vokabelmodus und lädt gespeicherte Werte.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        File vocabDir = new File("src/Trainer/Vocabsets");
        File[] files = vocabDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));
        if (files != null) {
            for (File f : files) {
                vocabListBox.getItems().add(f.getName());
            }
        }

        String savedFile = prefs.get("vocabFile", "defaultvocab.json");
        if (vocabListBox.getItems().contains(savedFile)) {
            vocabListBox.setValue(savedFile);
        }

        updateVocabModes();

        String savedMode = prefs.get("vocabMode", vocabModeBox.getValue());
        if (vocabModeBox.getItems().contains(savedMode)) {
            vocabModeBox.setValue(savedMode);
        }

        vocabModeBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) ->
                prefs.put("vocabMode", newVal));
        vocabListBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            prefs.put("vocabFile", newVal);
            updateVocabModes();
        });
    }

    /**
     * Beispiel-Handler für den Startknopf.
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

}
