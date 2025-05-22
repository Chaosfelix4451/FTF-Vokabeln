package Trainer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import Utils.SceneLoader;

public class TrainerController {

    private Stage stage = null;

    @FXML
    private AnchorPane rootPane;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        // Buttons erzeugen
        Button closeButton = new Button("Schließen");
        closeButton.setOnAction(this::openMainMenu);

        Button changeColorButton = new Button("Weiter");
        //changeColorButton.setOnAction(this::handleChangeColorButtonPressEvent);

        // View erzeugen und UI einbauen
        TrainerView view = new TrainerView("Start-Vokabel", closeButton, changeColorButton);
        view.buildUI(rootPane);
    }

    @FXML
    public void openMainMenu(ActionEvent event) {
        SceneLoader.load("/MainMenu/mainMenu.fxml");
    }

}
