package Trainer.old;

import Trainer.TrainerModel;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.util.ArrayList;
import java.util.List;

/**
 * Alte Implementierung der Trainingsansicht zur Referenz.
 */
public class TrainerView_old {

    private final TrainerModel model;
    private final List<TextField> inputFields = new ArrayList<>();
    private final List<String> shownIds = new ArrayList<>();
    private String questionLang = "en";
    private String answerLang = "de";

    public TrainerView_old(TrainerModel model) {
        this.model = model;
    }

//+
//    public List<TextField> getInputFields() {
//        return inputFields;
//    }
//
//    public List<String> getShownIds() {
//        return shownIds;
//    }

    public void setLanguageMode(String questionLang, String answerLang) {
        this.questionLang = questionLang;
        this.answerLang = answerLang;
    }

    /**
     * Baut eine einfache Benutzeroberfläche für die Trainingsrunde auf.
     */
    public void buildUI(GridPane rootPane) {
        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setAlignment(Pos.TOP_CENTER);

        GridPane vocabGrid = new GridPane();
        vocabGrid.setHgap(10);
        vocabGrid.setVgap(10);

        Set<String> allIds = model.getAllIds();
        if (allIds.isEmpty()) return;

        List<String> ids = new ArrayList<>(allIds);
        Collections.shuffle(ids);

        int count = Math.min(10, Math.max(3, ids.size()));
        List<String> selectedIds = ids.subList(0, count);

        shownIds.clear();
        inputFields.clear();

        for (int i = 0; i < selectedIds.size(); i++) {
            String id = selectedIds.get(i);
            shownIds.add(id);

            String question = model.get(id, questionLang);

            Label outputField = new Label((i + 1) + ". " + question);
            outputField.setMinWidth(150);

            TextField inputField = new TextField();
            inputField.setPromptText("Antwort eingeben...");
            inputField.setMinWidth(200);

            inputFields.add(inputField);

            vocabGrid.add(outputField, 0, i + 1);
            vocabGrid.add(inputField, 1, i + 1);
        }

        mainLayout.getChildren().add(vocabGrid);
        rootPane.getChildren().add(mainLayout);
    }
}
