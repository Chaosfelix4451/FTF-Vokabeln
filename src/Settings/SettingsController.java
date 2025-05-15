package Settings;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;

public class SettingsController {

    // Member variable holding a reference to the respective scene (window).
    private Stage stage = null;
    // Member variable holding a reference to the next scene the controller should switch to.
    private Scene nextScene = null;
    // Member variable for implementing a simple color switch.
    private boolean colorChangeButtonPressed = false;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setNextScene(Scene nextScene) {
        this.nextScene = nextScene;
    }

    @FXML
    private Button button;
    public void initialisere(){
        button.setAlignment(Pos.CENTER);

    }
    @FXML
    private Button changeColorButton;

    /**
     * Button press handle, which changes the current scene.
     * @param event
     */
    @FXML
    private void handleSettingsMenuViewButtonPressEvent(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Settings/Settings.fxml"));
            Parent root = loader.load();

            // Optional: Controller-Setup (z. B. Stage übergeben)
            //MainMenu.MainMenuController controller = loader.getController();
            //controller.setStage(stage);

            //Scene mainScene = new Scene(root);
            //stage.setScene(mainScene);

        } catch (IOException e) {
            e.printStackTrace(); // oder besser: Logger verwenden
        }
    }

    /**
     * Button press handle, which changes the background color of the root element.
     * @param event
     */
    @FXML
    private void handleChangeColorButtonPressEvent(ActionEvent event) {
        Parent root = stage.getScene().getRoot();
        if (colorChangeButtonPressed) {
            root.setStyle("-fx-background-color: WHITESMOKE");
        } else {
            root.setStyle("-fx-background-color: LIGHTGREEN");
        }
        colorChangeButtonPressed = !colorChangeButtonPressed;
    }

}