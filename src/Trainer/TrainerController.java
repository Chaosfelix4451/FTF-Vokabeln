package Trainer;

import Utils.Sound.SoundModel;
import Utils.UserScore.UserSystem;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import Utils.SceneLoader.SceneLoader;


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
    private final SoundModel soundModel = new SoundModel();
    private final List<VocabEntry> vocabEntries = new ArrayList<>();
    private final UserSystem userManager = new UserSystem();
    private final String currentUser = "user";
    private int currentIndex = 0;
    private int questionsPerRound = 5;
    private Stage stage;

    private static class VocabEntry {
        String solution;
        TextField inputField;
        HBox container;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void initialize() {
        userManager.addUser(currentUser);
        loadNextVocabSet();

        nextButton.setOnAction(event -> checkAnswers());

        backButton.setOnAction(event -> SceneLoader.load(stage, "/MainMenu/mainMenu.fxml"));
    }

    private void loadNextVocabSet() {
        vocabBox.getChildren().clear();
        vocabEntries.clear();

        questionsPerRound = ThreadLocalRandom.current().nextInt(3, Math.min(10, model.getSize()) + 1);
        int endIndex = Math.min(currentIndex + questionsPerRound, model.getSize());

        for (int i = currentIndex; i < endIndex; i++) {
            final String sourceWord = model.get(i);
            final String solutionWord = model.getTranslation(i);

            Label vocabLabel = new Label(sourceWord);
            vocabLabel.setMinWidth(150);

            TextField input = new TextField();
            input.setPromptText("Vokabel eingeben...");
            input.setMinWidth(200);

            HBox entryBox = new HBox(10, vocabLabel, input);

            VocabEntry entry = new VocabEntry();
            entry.solution = solutionWord;
            entry.inputField = input;
            entry.container = entryBox;

            vocabEntries.add(entry);
            vocabBox.getChildren().add(entryBox);
        }

        if (stage != null) {
            Platform.runLater(stage::sizeToScene);
        }
    }

    public void checkAnswers() {
        int correctCount = 0;
        userManager.startNewSession(currentUser, null);

        for (VocabEntry entry : vocabEntries) {
            String expected = entry.solution;
            String userInput = entry.inputField.getText().trim();

            HBox resultBox = new HBox(5);
            int len = Math.max(expected.length(), userInput.length());
            for (int i = 0; i < len; i++) {
                String charStr = i < userInput.length() ? String.valueOf(userInput.charAt(i)) : "";
                Label label = new Label(charStr);
                label.setMinWidth(30);
                label.setAlignment(Pos.CENTER);

                if (i < expected.length() && i < userInput.length() &&
                        charStr.equalsIgnoreCase(String.valueOf(expected.charAt(i)))) {
                    label.setStyle("-fx-background-color: lightgreen;");
                } else {
                    label.setStyle("-fx-background-color: salmon;");
                }

                resultBox.getChildren().add(label);
            }

            entry.container.getChildren().remove(entry.inputField);
            entry.container.getChildren().add(resultBox);

            boolean isCorrect = userInput.equalsIgnoreCase(expected);

            if (isCorrect) {
                soundModel.playSound("src/Utils/Sound/richtig.mp3");
                userManager.addPoint(currentUser);
                correctCount++;
            } else {
                soundModel.playSound("src/Utils/Sound/falsch.mp3");
            }
            userManager.recordAnswer(currentUser, isCorrect, null);
        }

        userManager.saveToFile();

        nextButton.setDisable(true);

        if (stage != null) {
            Platform.runLater(stage::sizeToScene);
        }

        Thread delayThread = new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            Platform.runLater(() -> {
                currentIndex += questionsPerRound;
                if (currentIndex < model.getSize()) {
                    loadNextVocabSet();
                    nextButton.setDisable(false);
                } else {
                    nextButton.setDisable(false);
                    nextButton.setText("Ergebnisse anzeigen");
                    nextButton.setOnAction(event -> SceneLoader.load(stage, "/MainMenu/mainMenu.fxml"));
                }
            });
        });
        delayThread.start();
    }
}
