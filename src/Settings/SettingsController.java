package Settings;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import Utils.SceneLoader;


import java.io.IOException;

public class SettingsController {
    /* Need for Stage change over functions include this code block in every Controller Class
    Pls do not delete or change
     */

    private Stage stage = null;
    //@Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    ////////////////////////

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
}