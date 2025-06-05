package MainMenu;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import Utils.SceneLoader.SceneLoader;

public class MainMenuController {

     /* Need for Stage change over functions include this code block in every Controller Class
    Pls do not delete or change
     */

    private Stage stage = null;
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    ////////////////////////
    @FXML
    private Button button;

    @FXML
    private VBox sideMenu;

    @FXML
    public void openTrainer(ActionEvent event) {
        SceneLoader.load("/Trainer/Trainer.fxml");
    }
    public void openTrainerView(ActionEvent event) {
        SceneLoader.load("/Trainer/TrainerView.fxml");
    }
    public void openSettings(ActionEvent event) {
        SceneLoader.load("/Settings/Settings.fxml");
    }
    public void openUserManagement(ActionEvent event) {
        SceneLoader.load("/UserManagement/UserManagement.fxml");
    }

    public void openScoreBoard(ActionEvent event) {
        SceneLoader.load("/ScoreBoard/ScoreBoard.fxml");
    }

    @FXML
    private void toggleSideMenu() {
        boolean isVisible = sideMenu.isVisible();
        sideMenu.setVisible(!isVisible);
        sideMenu.setManaged(!isVisible);
    }

    @FXML
    private void handleExit() {
        Platform.exit();
    }
}
