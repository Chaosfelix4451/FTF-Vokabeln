package Settings;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class SettingsController {

    private Stage stage = null;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private Button button;

    @FXML
    private void handleTrainerViewButtonPressEvent(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Trainer/Trainer.fxml"));
            Parent root = loader.load();

            Trainer.TrainerController controller = loader.getController();
            controller.setStage(stage);

            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSettingsMenuViewButtonPressEvent(ActionEvent event) {
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
}