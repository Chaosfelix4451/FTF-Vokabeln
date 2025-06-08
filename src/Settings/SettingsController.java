package Settings;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
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

    /**
     * Initialisiert die ComboBox für den Vokabelmodus und lädt gespeicherte Werte.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        vocabModeBox.getItems().addAll(
                "Deutsch zu Englisch",
                "Englisch zu Deutsch",
                "Zufällig"
        );

        String savedMode = prefs.get("vocabMode", "Deutsch zu Englisch");
        vocabModeBox.setValue(savedMode);

        vocabModeBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) ->
                prefs.put("vocabMode", newVal));
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
