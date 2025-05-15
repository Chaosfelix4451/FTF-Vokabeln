package Trainer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class TrainerController {

    private Stage stage = null;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private Button button;

    public void initialisere() {
        button.setAlignment(Pos.CENTER);
    }

    @FXML
    private Button changeColorButton;

    @FXML
    private void handleButtonPressEvent(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainMenu/mainMenu.fxml"));
            Parent root = loader.load();

            MainMenu.MainMenuController controller = loader.getController();
            controller.setStage(stage);

            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
