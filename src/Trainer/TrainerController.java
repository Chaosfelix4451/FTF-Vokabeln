package Trainer;

import Utils.Sound.SoundModel;
import Utils.UserScore.UserSystem;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
        List<TextField> fields = new ArrayList<>();
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

            final HBox letterBox = new HBox(5);
            final VocabEntry entry = new VocabEntry();
            entry.solution = solutionWord;

            for (int j = 0; j < solutionWord.length(); j++) {
                final int index = j;
                final TextField field = new TextField();
                field.setPrefWidth(30);
                field.setMaxWidth(30);

                field.setOnKeyTyped(event -> {
                    String character = event.getCharacter();
                    if (!character.matches("[a-zA-ZäöüÄÖÜß]")) {
                        event.consume();
                        return;
                    }

                    field.setText(character);
                    event.consume();

                    Platform.runLater(() -> {
                        field.positionCaret(1);
                        if (index < solutionWord.length() - 1) {
                            entry.fields.get(index + 1).requestFocus();
                        }
                    });
                });

                field.setOnKeyPressed(event -> {
                    if (event.getCode() == KeyCode.BACK_SPACE && field.getText().isEmpty() && index > 0) {
                        final TextField prev = entry.fields.get(index - 1);
                        Platform.runLater(() -> {
                            prev.requestFocus();
                            prev.clear();
                        });
                        event.consume();
                    }
                });

                entry.fields.add(field);
                letterBox.getChildren().add(field);
            }

            vocabEntries.add(entry);
            vocabBox.getChildren().add(new HBox(10, vocabLabel, letterBox));
        }
    }

    public void checkAnswers() {
        int correctCount = 0;
        userManager.startNewSession(currentUser, null);

        for (VocabEntry entry : vocabEntries) {
            String expected = entry.solution;
            StringBuilder userInput = new StringBuilder();
            for (TextField field : entry.fields) {
                userInput.append(field.getText().trim());
            }

            boolean isCorrect = userInput.toString().equalsIgnoreCase(expected);

            for (int i = 0; i < entry.fields.size(); i++) {
                TextField field = entry.fields.get(i);
                String input = field.getText().trim();
                char expectedChar = expected.charAt(i);

                if (input.equalsIgnoreCase(String.valueOf(expectedChar))) {
                    field.setStyle("-fx-background-color: lightgreen;");
                } else {
                    field.setStyle("-fx-background-color: salmon;");
                }

                field.setEditable(false);
            }

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
