package Settings;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import Utils.SceneLoader.SceneLoader;
import Utils.StageAwareController;
import Trainer.TrainerModel;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.prefs.Preferences;

public class SettingsController extends StageAwareController implements Initializable {

    private final Preferences prefs = Preferences.userNodeForPackage(SettingsController.class);

    public Label mainLable;
    public Button Button;
    public Button button;
    public CheckBox darkModeToggle;
    public Label vDark;

    private String darkCss;

    @FXML
    private ChoiceBox<String> vocabModeBox;
    @FXML
    private ChoiceBox<String> vocabListBox;

    @FXML
    public void openMainMenu(ActionEvent event) {
        SceneLoader.load("/MainMenu/mainMenu.fxml");
    }

    @FXML
    public void openTrainer(ActionEvent event) {
        SceneLoader.load(stage, "/Trainer/Trainer.fxml");
    }

    private void updateVocabModes() {
        String file = vocabListBox.getValue();
        if (file == null) return;
        TrainerModel model = new TrainerModel();
        model.LoadJSONtoDataObj("src/Trainer/Vocabsets/" + file);
        List<String> options = model.generateAllLanguageModes();
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

        String savedMode = prefs.get("vocabMode", "Zufällig");
        if (vocabModeBox.getItems().contains(savedMode)) {
            vocabModeBox.setValue(savedMode);
        }

        vocabModeBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                prefs.put("vocabMode", newVal);
            }
        });

        vocabListBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                prefs.put("vocabFile", newVal);
            }
            updateVocabModes();
            String mode = vocabModeBox.getValue();
            if (mode != null) {
                prefs.put("vocabMode", mode);
            }
        });

        darkCss = getClass().getResource("/dark.css").toExternalForm();
        boolean dark = prefs.getBoolean("darkMode", false);
        darkModeToggle.setSelected(dark);
        darkModeToggle.selectedProperty().addListener((obs, o, n) -> {
            prefs.putBoolean("darkMode", n);
            applyDarkMode();
        });

        javafx.application.Platform.runLater(this::applyDarkMode);
    }

    @FXML
    private void handleStart(ActionEvent event) {
        String auswahl = vocabModeBox.getValue();
        switch (auswahl) {
            case "Deutsch zu Englisch":
                break;
            case "Englisch zu Deutsch":
                break;
            case "Zufällig":
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
