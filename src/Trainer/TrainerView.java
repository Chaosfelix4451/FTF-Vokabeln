package Trainer;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;

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
        int i;
        rootPane.setPadding(new Insets(10));
        FlowPane flowPane = new FlowPane(10, 10);
        for(i=0; i<10; i++) {
            HBox hbox = new HBox(10);
            hbox.setPadding(new Insets(10));

            TextField outputField = new TextField(initialText);
            TextField inputField = new TextField("Vokabel Eingabe");

            hbox.getChildren().addAll(outputField, closeButton, nextbutton, inputField);
            rootPane.getChildren().add(hbox);
        }
    }
}
