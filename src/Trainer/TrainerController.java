package Trainer;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import Utils.SceneLoader;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class TrainerController {

    @FXML
    private VBox vocabBox;
    @FXML
    private Button backButton;
    @FXML
    private Button nextButton;

    private final TrainerModel model = new TrainerModel();
    private final Map<TextField, String> fieldToVocab = new HashMap<>();
    private final List<TextField> inputFields = new ArrayList<>();
    private int currentIndex = 0;
    private int questionsPerRound = 5;
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void initialize() {
        loadNextVocabSet();

        nextButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                checkAnswersAndMaybeContinue();
            }
        });

        backButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                SceneLoader.load(stage, "/src/MainMenu/mainMenu.fxml");
            }
        });
    }

    private void loadNextVocabSet() {
        vocabBox.getChildren().clear();
        fieldToVocab.clear();
        inputFields.clear();

        // Zufällige Anzahl an Fragen
        questionsPerRound = ThreadLocalRandom.current().nextInt(3, Math.min(10, model.getSize()) + 1);
        int endIndex = Math.min(currentIndex + questionsPerRound, model.getSize());

        for (int i = currentIndex; i < endIndex; i++) {
            String vocab = model.get(i);
            Label vocabLabel = new Label(vocab);
            vocabLabel.setMinWidth(150);

            TextField inputField = new TextField();
            fieldToVocab.put(inputField, vocab);
            inputFields.add(inputField);

            HBox hBox = new HBox(10, vocabLabel, inputField);
            vocabBox.getChildren().add(hBox);
        }
    }

    private void checkAnswersAndMaybeContinue() {
        boolean allFilled = true;
        boolean allCorrect = true;

        for (TextField field : inputFields) {
            String input = field.getText().trim();
            String expected = fieldToVocab.get(field);

            if (input.isEmpty()) {
                field.setStyle("-fx-background-color: red;");
                allFilled = false;
            } else {
                if (input.equalsIgnoreCase(expected)) {
                    field.setStyle("-fx-background-color: lightgreen;");
                } else {
                    field.setStyle("-fx-background-color: salmon;");
                    allCorrect = false;
                }
            }
        }

        if (!allFilled) return;

        // Sperre aktivieren
        nextButton.setDisable(true);

        if (allCorrect) {
            // 3 Sekunden warten, dann weiter
            new Thread(() -> {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                Platform.runLater(() -> {
                    currentIndex += questionsPerRound;
                    if (currentIndex < model.getSize()) {
                        loadNextVocabSet();
                        nextButton.setDisable(false); // Sperre aufheben
                    }
                });
            }).start();
        } else {
            // Bei Fehlern sofort wieder freigeben
            nextButton.setDisable(false);
        }
    }

}
