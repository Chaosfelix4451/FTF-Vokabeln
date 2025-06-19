package Trainer;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class TrainerView {


    private  TrainerModel model;
    private  List<TextField> inputFields = new ArrayList<>();
    private List<Integer> shownIndices = new ArrayList<>();
    public TrainerView(TrainerModel model) {
        this.model = model;
    }

    public List<TextField> getInputFields() {
        return inputFields;
    }

    public List<Integer> getShownIndices() {
        return shownIndices;
    }

    public void buildUI(GridPane rootPane) {
        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setAlignment(Pos.TOP_CENTER);

        GridPane vocabGrid = new GridPane();
        vocabGrid.setHgap(10);
        vocabGrid.setVgap(10);

        int maxCount = model.getSize();
        int count = ThreadLocalRandom.current().nextInt(3, Math.min(10, maxCount) + 1); // max 10

        for (int i = 0; i < count; i++) {
            Label outputField = new Label(model.getEnglish(i));
            outputField.setMinWidth(150);

            TextField inputField = new TextField();
            inputField.setPromptText("Vokabel eingeben...");
            inputField.setMinWidth(200);

            vocabGrid.add(outputField, 0, i + 1);
            vocabGrid.add(inputField, 1, i + 1);
        }

        mainLayout.getChildren().add(vocabGrid);
        rootPane.getChildren().add(mainLayout);
    }
}
