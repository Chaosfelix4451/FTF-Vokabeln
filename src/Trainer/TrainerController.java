package Trainer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import Utils.SceneLoader;

import java.io.IOException;

public class TrainerController {
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

    public void initialisere() {
        button.setAlignment(Pos.CENTER);
    }

    @FXML
    private Button changeColorButton;

    @FXML
    public void openMainMenu(ActionEvent event) {
        SceneLoader.load("/MainMenu/mainMenu.fxml");
    }

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

    private boolean colorChangeButtonPressed = false;
}
