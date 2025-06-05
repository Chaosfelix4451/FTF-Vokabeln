package MainMenu;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
//import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import Utils.SceneLoader.SceneLoader;
import Utils.UserScore.UserSystem;
import Utils.StageAwareController;

/**
 * Startmenü des Vokabeltrainers.
 */
public class MainMenuController extends StageAwareController {

    // Stage wird über die Basisklasse gesetzt
//    @FXML
//    private Button button;

    @FXML
    private TextField userField;

    @FXML
    private VBox sideMenu;
    private ActionEvent event;

    @FXML
    private void initialize() {
        UserSystem.loadFromFile();
        if (userField != null) {
            userField.setText(UserSystem.getCurrentUser());
        }
    }


    @FXML

    public void openTrainer(ActionEvent event) {
        this.event = event;
        prepareUser();
        SceneLoader.load("/Trainer/Trainer.fxml");
    }
    /**
     * Öffnet die Einstellungen.
     */
    public void openSettings(ActionEvent event) {
        this.event = event;
        SceneLoader.load("/Settings/Settings.fxml");
    }

    /**
     * Öffnet die Benutzerverwaltung.
     */
    public void openUserManagement(ActionEvent event) {
        this.event = event;
        SceneLoader.load("/UserManagement/UserManagement.fxml");
    }

    /**
     * Zeigt die Highscore-Tabelle an.
     */
    public void openScoreBoard(ActionEvent event) {
        this.event = event;
        prepareUser();
        SceneLoader.load("/ScoreBoard/ScoreBoard.fxml");
    }

    /**
     * Blendet das seitliche Menü ein oder aus.
     */
    @FXML
    private void toggleSideMenu() {
        boolean isVisible = sideMenu.isVisible();
        sideMenu.setVisible(!isVisible);
        sideMenu.setManaged(!isVisible);
    }

    /**
     * Beendet die Anwendung.
     */
    @FXML
    private void handleExit() {
        Platform.exit();
    }

    private void prepareUser() {
        if (userField == null) {
            return;
        }
        String name = userField.getText().trim();
        if (name.isEmpty()) {
            name = "user";
        }
        if (!UserSystem.userExists(name)) {
            UserSystem.addUser(name);
        }
        UserSystem.setCurrentUser(name);
        UserSystem.saveToFile();
    }


    public ActionEvent getEvent() {
        return event;
    }

    public void setEvent(ActionEvent event) {
        this.event = event;
    }
}
