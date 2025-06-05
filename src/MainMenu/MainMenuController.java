package MainMenu;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import Utils.SceneLoader.SceneLoader;
import Utils.StageAwareController;

/**
 * Startmenü des Vokabeltrainers.
 */
public class MainMenuController extends StageAwareController {

    // Stage wird über die Basisklasse gesetzt
    @FXML
    private Button button;

    @FXML
    private VBox sideMenu;

    @FXML
    /**
     * Öffnet den eigentlichen Trainer.
     */
    public void openTrainer(ActionEvent event) {
        SceneLoader.load("/Trainer/Trainer.fxml");
    }
    /**
     * Öffnet die Einstellungen.
     */
    public void openSettings(ActionEvent event) {
        SceneLoader.load("/Settings/Settings.fxml");
    }

    /**
     * Öffnet die Benutzerverwaltung.
     */
    public void openUserManagement(ActionEvent event) {
        SceneLoader.load("/UserManagement/UserManagement.fxml");
    }

    /**
     * Zeigt die Highscore-Tabelle an.
     */
    public void openScoreBoard(ActionEvent event) {
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
}
