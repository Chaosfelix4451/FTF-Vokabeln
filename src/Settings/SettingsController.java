package Settings;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;
import Utils.SceneLoader.SceneLoader;


import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class SettingsController implements Initializable {
    /* Need for Stage change over functions include this code block in every Controller Class
    Pls do not delete or change
     */

    private Stage stage = null;


    //@Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /// /////////////////////
    private final Preferences prefs = Preferences.userNodeForPackage(SettingsController.class);
    @FXML
    private Button button;

    @FXML
    public void openMainMenu(ActionEvent event) {
        SceneLoader.load("/MainMenu/mainMenu.fxml");
    }

    @FXML
    public void openTrainer(ActionEvent event) {
        SceneLoader.load(stage, "/Trainer/Trainer.fxml");
    }

    @FXML
    private ChoiceBox<String> vocabModeBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        vocabModeBox.getItems().addAll(
                "Deutsch zu Englisch",
                "Englisch zu Deutsch",
                "Zufällig"
        );

        String savedMode = prefs.get("vocabMode", "Deutsch zu Englisch");
        vocabModeBox.setValue(savedMode);

        vocabModeBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            prefs.put("vocabMode", newVal);
            System.out.println("Modus gespeichert: " + newVal);
        });
    }

    @FXML
    private void handleStart(ActionEvent event) {
        String auswahl = vocabModeBox.getValue();
        System.out.println("Ausgewählter Modus: " + auswahl);

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