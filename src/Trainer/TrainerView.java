package Trainer;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

// Die View ist zuständig für Layout und Aufbau
public class TrainerView {

    private final String initialText;
    private final Button nextbutton;
    private final Button closeButton;

    public TrainerView(String initialText, Button closeButton, Button changeColorButton) {
        this.initialText = initialText;
        this.closeButton = closeButton;
        this.nextbutton = changeColorButton;
    }

    public void buildUI(AnchorPane rootPane) {
        FlowPane vocabFlowPane = new FlowPane(10, 10);
        vocabFlowPane.setPrefWrapLength(500);
        vocabFlowPane.setPadding(new Insets(10));
        vocabFlowPane.setAlignment(Pos.CENTER);

        for (int i = 0; i < 10; i++) {
            HBox hbox = new HBox(10);
            hbox.setPadding(new Insets(10));
            hbox.setAlignment(Pos.CENTER);
            Label outputField = new Label(initialText);
            outputField.setAlignment(Pos.CENTER_LEFT);
            TextField inputField = new TextField("Vokabel Eingabe");
            inputField.setAlignment(Pos.CENTER_RIGHT);
            hbox.getChildren().addAll(outputField, inputField);
            vocabFlowPane.getChildren().add(hbox);
        }

        HBox buttonBar = new HBox(10);
        buttonBar.setPadding(new Insets(10));
        buttonBar.setAlignment(Pos.CENTER);
        buttonBar.getChildren().addAll(nextbutton, closeButton);

        VBox vbox = new VBox(20); // Abstand zwischen Vocab und Buttons
        vbox.setPadding(new Insets(10));
        vbox.getChildren().addAll(vocabFlowPane, buttonBar);
        vbox.setAlignment(Pos.CENTER);

        rootPane.getChildren().add(vbox);
        AnchorPane.setTopAnchor(vbox, 0.0);
        AnchorPane.setLeftAnchor(vbox, 0.0);
        AnchorPane.setRightAnchor(vbox, 0.0);
        AnchorPane.setBottomAnchor(vbox, 0.0);
    }
}
