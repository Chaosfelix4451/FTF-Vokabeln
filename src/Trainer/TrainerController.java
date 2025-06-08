package Trainer;

import Utils.Sound.SoundModel;
import Utils.UserScore.UserSystem;
import Settings.SettingsController;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import Utils.SceneLoader.SceneLoader;
import Utils.StageAwareController;


import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Steuert den Ablauf des Trainings und wertet die Antworten aus.
 */
public class TrainerController extends StageAwareController {

    @FXML
    private VBox vocabBox;
    @FXML
    private Button backButton;
    @FXML
    private Button nextButton;

    private final TrainerModel model = new TrainerModel();
    private final SoundModel soundModel = new SoundModel();
    private final List<VocabEntry> vocabEntries = new ArrayList<>();
    private String currentUser = "user";
    private String mode = "Englisch zu Deutsch";
    private int currentIndex = 0;
    private int questionsPerRound = 5;

    private static class VocabEntry {
        String solution;
        TextField inputField;
        HBox container;
    }

    /**
     * Initialisiert den Trainer und lädt die ersten Vokabeln.
     */
    @FXML
    private void initialize() {
        UserSystem.loadFromFile();
        currentUser = UserSystem.getCurrentUser();
        UserSystem.addUser(currentUser);
        UserSystem.startNewSession(currentUser, null);

        mode = java.util.prefs.Preferences.userNodeForPackage(SettingsController.class)
                .get("vocabMode", "Deutsch zu Englisch");

        loadNextVocabSet();

        nextButton.setOnAction(event -> checkAnswers());

        backButton.setOnAction(event -> SceneLoader.load(stage, "/MainMenu/mainMenu.fxml"));
    }

    /**
     * Baut die nächste Liste an Vokabeln entsprechend des gewählten Modus auf.
     */
    private void loadNextVocabSet() {
        vocabBox.getChildren().clear();
        vocabEntries.clear();

        questionsPerRound = ThreadLocalRandom.current().nextInt(3, Math.min(10, model.getSize()) + 1);
        int endIndex = Math.min(currentIndex + questionsPerRound, model.getSize());

        for (int i = currentIndex; i < endIndex; i++) {
            String sourceWord;
            String solutionWord;
            if ("Deutsch zu Englisch".equals(mode)) {
                sourceWord = model.getTranslation(i);
                solutionWord = model.get(i);
            } else if ("Englisch zu Deutsch".equals(mode)) {
                sourceWord = model.get(i);
                solutionWord = model.getTranslation(i);
            } else { // Zufällig
                if (ThreadLocalRandom.current().nextBoolean()) {
                    sourceWord = model.get(i);
                    solutionWord = model.getTranslation(i);
                } else {
                    sourceWord = model.getTranslation(i);
                    solutionWord = model.get(i);
                }
            }

            Label vocabLabel = new Label(sourceWord);
            vocabLabel.setMinWidth(150);

            TextField input = new TextField();
            input.setPromptText("Vokabel eingeben...");
            input.setMinWidth(200);
            int currentIndexForEnter = i - currentIndex;
            input.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ENTER) {
                    if (currentIndexForEnter + 1 < vocabEntries.size()) {
                        vocabEntries.get(currentIndexForEnter + 1).inputField.requestFocus();
                    } else {
                        nextButton.requestFocus();
                    }
                    event.consume();
                }
            });

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

    /**
     * Prüft alle Eingaben und zeigt richtige bzw. falsche Buchstaben an.
     */
    public void checkAnswers() {
        boolean allCorrect = true;
        for (VocabEntry entry : vocabEntries) {
            String expected = entry.solution;
            String userInput = entry.inputField.getText().trim();

            HBox resultBox = new HBox(5);
            int len = Math.max(expected.length(), userInput.length());
            for (int i = 0; i < len; i++) {
                String charStr = i < userInput.length() ? String.valueOf(userInput.charAt(i)) : "";
                Label label = new Label(charStr);
                label.setMaxWidth(30);
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
            if (!isCorrect) {
                allCorrect = false;
            }

            if (isCorrect) {
                UserSystem.addPoint(currentUser);
            }
            UserSystem.recordAnswer(currentUser, isCorrect, null);
        }

        // Spiele nur EINEN Sound ab, basierend auf dem Gesamtergebnis
        if (allCorrect) {
            soundModel.playSound("src/Utils/Sound/richtig.mp3");
        } else {
            soundModel.playSound("src/Utils/Sound/falsch.mp3");
        }

        UserSystem.saveToFile();

        nextButton.setDisable(true);

        if (stage != null) {
            Platform.runLater(stage::sizeToScene);
        }

        Thread delayThread = new Thread(() -> {
            try {
                Thread.sleep(5000);
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
                    nextButton.setOnAction(event -> SceneLoader.load(stage, "/ScoreBoard/ScoreBoard.fxml"));
                }
            });
        });
        delayThread.start();
    }
}
