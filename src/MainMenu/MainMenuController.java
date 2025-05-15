package MainMenu;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;


public class MainMenuController {

    // Member variable holding a reference to the respective scene (window).
    private Stage stage = null;
    // Member variable holding a reference to the next scene the controller should switch to.
    private Scene nextScene = null;


    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setNextScene(Scene nextScene) {
        this.nextScene = nextScene;
    }

    @FXML
    private Button button;

    /**
     * Button press handle, which changes the current scene.
     * @param event
     */
    @FXML
    private void handleTrainerViewButtonPressEvent(ActionEvent event) {
        stage.setScene(nextScene);
    }
    @FXML
    private void handleSettingsMenuViewButtonPressEvent(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Settings/Settings.fxml"));
            Parent root = loader.load();

            // Optional: Controller-Setup (z. B. Stage übergeben)
            //MainMenu.MainMenuController controller = loader.getController();
            //controller.setStage(stage);

            Scene mainScene = new Scene(root);
            stage.setScene(mainScene);

        } catch (IOException e) {
            e.printStackTrace(); // oder besser: Logger verwenden
        }
    }
    @FXML
    private VBox sideMenu;

    @FXML
    private void toggleSideMenu() {
        boolean isVisible = sideMenu.isVisible();
        sideMenu.setVisible(!isVisible);
        sideMenu.setManaged(!isVisible);
    }

    @FXML
    private void handleExit() {
        System.exit(0);
    }
}