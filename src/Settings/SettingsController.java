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

    private String darkCss;

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

    /**
     * Initialisiert die ComboBox für den Vokabelmodus und lädt gespeicherte Werte.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        vocabModeBox.getItems().addAll(
                "Deutsch zu Englisch",
                "Englisch zu Deutsch",
                "Deutsch zu Französisch",
                "Französisch zu Deutsch",
                "Deutsch zu Spanisch",
                "Spanisch zu Deutsch",
                "Zufällig"
        );


        File vocabDir = new File("src/Trainer/Vocabsets");
        File[] files = vocabDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));
        if (files != null) {
            for (File f : files) {
                vocabListBox.getItems().add(f.getName());
            }
        }

        String savedMode = prefs.get("vocabMode", "Deutsch zu Englisch");
        vocabModeBox.setValue(savedMode);

        String savedFile = prefs.get("vocabFile", "defaultvocab.json");
        if (vocabListBox.getItems().contains(savedFile)) {
            vocabListBox.setValue(savedFile);
        }

        vocabModeBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) ->
                prefs.put("vocabMode", newVal));
        vocabListBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) ->
                prefs.put("vocabFile", newVal));

        darkCss = getClass().getResource("/dark.css").toExternalForm();
        boolean dark = prefs.getBoolean("darkMode", false);
        darkModeToggle.setSelected(dark);
        darkModeToggle.selectedProperty().addListener((obs, o, n) -> {
            prefs.putBoolean("darkMode", n);
            applyDarkMode();
        });

        javafx.application.Platform.runLater(this::applyDarkMode);
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

    private void applyDarkMode() {
        if (stage == null) return;
        var scene = stage.getScene();
        if (scene == null) return;
        if (darkModeToggle.isSelected()) {
            if (!scene.getStylesheets().contains(darkCss)) {
                scene.getStylesheets().add(darkCss);
            }
        } else {
            scene.getStylesheets().remove(darkCss);
        }
    }

}
