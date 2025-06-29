package Trainer;

import Utils.Sound.SoundModel;
import Utils.UserSys.UserSys;
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

import java.nio.file.Path;
import java.util.*;

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
    @FXML
    private Button finishButton;
    @FXML
    private Label pointsLabel;

    private TrainerModel model;
    private String listId = "defaultvocab.json";
    private final SoundModel soundModel = new SoundModel();
    private final List<VocabEntry> vocabEntries = new ArrayList<>();
    private List<String> remainingIds = new ArrayList<>();
    private String currentUser = "user";
    private String mode = "Englisch zu Deutsch";
    private int questionsPerRound = 5;
    private int sessionPoints = 0;

    private static class VocabEntry {
        String solution;
        TextField inputField;
        HBox container;
        String difficulty;
    }

    @FXML
    private void initialize() {
        UserSys.loadFromJson();
        currentUser = UserSys.getCurrentUser();
        UserSys.addUser(currentUser);
        sessionPoints = 0;
        if (pointsLabel != null) {
            pointsLabel.setText("Punkte: 0");
        }

        UserSys.loadFromJson();
        mode = UserSys.getPreference("vocabMode", "Deutsch zu Englisch");
        listId = UserSys.getPreference("vocabFile", "defaultvocab.json");

        model = new TrainerModel();
        model.LoadJSONtoDataObj(listId);
        UserSys.getUser(currentUser).getStats(listId).startNewSession();

        remainingIds = new ArrayList<>(model.getAllIds());
        remainingIds.removeIf(id -> !model.hasValidTranslation(id, mode));

        Collections.shuffle(remainingIds);

        loadNextVocabSet();

        nextButton.setOnAction(event -> checkAnswers());
        finishButton.setOnAction(event -> finishTraining());
        backButton.setOnAction(event -> SceneLoader.load(stage, "/MainMenu/mainMenu.fxml"));
    }

    private void loadNextVocabSet() {
        vocabBox.getChildren().clear();
        vocabEntries.clear();

        if (remainingIds.isEmpty()) {
            finishTraining();
            return;
        }

        int count = Math.min(questionsPerRound, remainingIds.size());
        List<String> selectedIds = new ArrayList<>(remainingIds.subList(0, count));
        remainingIds.subList(0, count).clear();

        for (int i = 0; i < selectedIds.size(); i++) {
            String id = selectedIds.get(i);
            var langPair = model.getLangPairForMode(mode);
            String question = model.get(id, langPair[0]);
            String answer = model.get(id, langPair[1]);
            String diff = model.get(id, "difficulty");

            Label label = new Label((i + 1) + ". (" + langName(langPair[0]) + " -> " + langName(langPair[1]) + ") " + question);
            TextField input = new TextField();
            input.setPromptText("Antwort eingeben...");
            input.setMinWidth(200);

            int indexForEnter = i;
            input.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ENTER) {
                    if (indexForEnter + 1 < vocabEntries.size()) {
                        vocabEntries.get(indexForEnter + 1).inputField.requestFocus();
                    } else {
                        nextButton.requestFocus();
                    }
                    event.consume();
                }
            });

            HBox box = new HBox(10, label, input);
            VocabEntry entry = new VocabEntry();
            entry.solution = answer;
            entry.inputField = input;
            entry.container = box;
            entry.difficulty = diff;
            vocabEntries.add(entry);
            vocabBox.getChildren().add(box);
        }

        if (stage != null) {
            Platform.runLater(stage::sizeToScene);
        }
    }

    private String langName(String code) {
        return switch (code) {
            case "de" -> "Deutsch";
            case "en" -> "Englisch";
            case "fr" -> "Französisch";
            case "es" -> "Spanisch";
            default -> code;
        };
    }

    private int pointsForDifficulty(String diff) {
        if (diff == null) return 1;
        return switch (diff.toLowerCase()) {
            case "hard" -> 3;
            case "medium" -> 2;
            default -> 1;
        };
    }

    public void checkAnswers() {
        boolean allCorrect = true;
        int correctCount = 0;
        var stats = UserSys.getUser(currentUser).getStats(listId);

        for (VocabEntry entry : vocabEntries) {
            String userInput = entry.inputField.getText().trim();
            String expected = entry.solution;

            HBox resultBox = new HBox(5);
            int len = Math.max(expected.length(), userInput.length());
            for (int i = 0; i < len; i++) {
                String charStr = i < userInput.length() ? String.valueOf(userInput.charAt(i)) : "";
                Label label = new Label(charStr);
                label.setMinWidth(30);
                label.setAlignment(Pos.CENTER);
                label.setId("outputbox");
                label.setDisable(true);

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
            if (!isCorrect) allCorrect = false;

            stats.record(isCorrect);
            if (isCorrect) {
                int pts = pointsForDifficulty(entry.difficulty);
                UserSys.getUser(currentUser).addPoints(pts);
                sessionPoints += pts;
                correctCount++;
            }
        }

        if (pointsLabel != null) {
            pointsLabel.setText("Punkte: " + sessionPoints);
        }

        UserSys.saveToJson();

        double percent = (vocabEntries.isEmpty()) ? 0 : (correctCount * 100.0 / vocabEntries.size());
        if (percent >= 50.0) {
            soundModel.playSound("src/Utils/Sound/richtig.mp3");
        } else {
            soundModel.playSound("src/Utils/Sound/falsch.mp3");
        }

        nextButton.setDisable(true);
        Thread delayThread = new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            Platform.runLater(() -> {
                if (!remainingIds.isEmpty()) {
                    loadNextVocabSet();
                    nextButton.setDisable(false);
                } else {
                    finishTraining();
                }
            });
        });
        delayThread.start();
    }

    private void finishTraining() {
        if (stage != null) {
            SceneLoader.load(stage, "/ScoreBoard/ScoreBoard.fxml");
        } else {
            SceneLoader.load("/ScoreBoard/ScoreBoard.fxml");
        }
    }
}
