package Trainer;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import Trainer.TrainerController;

public class TrainerView {

    private final String initialText;
    private final Button nextButton;
    private final Button backButton;
    private String[] vocabel = TrainerController.getVocabel();

    public TrainerView(String initialText, Button backButton, Button nextButton) {
        this.initialText = initialText;
        this.backButton = backButton;
        this.nextButton = nextButton;
    }

    public void buildUI(AnchorPane rootPane) {
        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setAlignment(Pos.TOP_CENTER);

        // Grid für Vokabeln + Eingabe
        GridPane vocabGrid = new GridPane();
        vocabGrid.setHgap(10);
        vocabGrid.setVgap(10);
        vocabGrid.setPadding(new Insets(10));
        vocabGrid.setAlignment(Pos.CENTER);

        // Kopfzeile
        Label vocabLabel = new Label("Vokabel");
        Label inputLabel = new Label("Eingabe");
        vocabLabel.setStyle("-fx-font-weight: bold");
        inputLabel.setStyle("-fx-font-weight: bold");
        vocabGrid.add(vocabLabel, 0, 0);
        vocabGrid.add(inputLabel, 1, 0);

        // Vokabelzeilen
        for (int i = 0; i <= 2; i++) {
            Label outputField = new Label(vocabel[i]);
            outputField.setMinWidth(150);
            TextField inputField = new TextField();
            inputField.setPromptText("Vokabel eingeben...");
            inputField.setMinWidth(200);

            vocabGrid.add(outputField, 0, i + 1);
            vocabGrid.add(inputField, 1, i + 1);
        }

        // Button-Leiste
        HBox buttonBar = new HBox(20);
        buttonBar.setAlignment(Pos.CENTER);
        buttonBar.setPadding(new Insets(10));
        buttonBar.getChildren().addAll(backButton, nextButton);

        // Zusammenbauen
        mainLayout.getChildren().addAll(vocabGrid, buttonBar);

        // In Root einfügen
        rootPane.getChildren().add(mainLayout);
        AnchorPane.setTopAnchor(mainLayout, 0.0);
        AnchorPane.setBottomAnchor(mainLayout, 0.0);
        AnchorPane.setLeftAnchor(mainLayout, 0.0);
        AnchorPane.setRightAnchor(mainLayout, 0.0);
    }
}
