package Trainer;

import Utils.SceneLoader.SceneLoader;
import Utils.Sound.SoundModel;
import Utils.StageAwareController;
import Utils.UserSys.UserSys;
import Utils.Confetti.Confetti;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    @FXML
    private Pane confettiPane;
    @FXML
    private StackPane rootPane;

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

        if (confettiPane != null && rootPane != null) {
            confettiPane.prefWidthProperty().bind(rootPane.widthProperty());
            confettiPane.prefHeightProperty().bind(rootPane.heightProperty());
        }
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

        // Keep the window size from the previous scene
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
    private double calculatePercentage() {
        int correctCount = 0;
        for (VocabEntry entry : vocabEntries) {
            String userInput = entry.inputField.getText().trim();
            String expected = entry.solution;
            if (userInput.equalsIgnoreCase(expected)) {
                correctCount++;
            }
        }

        double prozentKorrekt = (vocabEntries.isEmpty()) ? 0 : (correctCount * 100.0 / vocabEntries.size());
        return prozentKorrekt;
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

        double prozentKorrekt = calculatePercentage();

        double percent = (vocabEntries.isEmpty()) ? 0 : (correctCount * 100.0 / vocabEntries.size());
        if (percent >= 75.0) {
            soundModel.playSound("src/Utils/Sound/richtig.mp3");
        } else if (percent >= 50.0) {
            soundModel.playSound("src/Utils/Sound/teilweise.mp3");
        } else {
            soundModel.playSound("src/Utils/Sound/falsch.mp3");
        }


        nextButton.setDisable(true); // Button deaktivieren

        Runnable naechsteAktion = new Runnable() {
            @Override
            public void run() {
                if (!remainingIds.isEmpty()) {
                    loadNextVocabSet();
                    nextButton.setDisable(false);
                } else {
                    if (calculatePercentage() >= 90.0) {
                        Platform.runLater(() -> {
                            // Zeige Konfetti und spiele Sound ab bei >=75%
                            Confetti.show(confettiPane != null ? confettiPane : rootPane);
                            soundModel.playSound("src/Utils/Sound/super.mp3");
                            
                            // Warte 5 Sekunden, dann zum Highscore
                            Thread delayThread = new Thread(() -> {
                                try {
                                    Thread.sleep(5000);
                                    Platform.runLater(() -> finishTraining());
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                }
                            });
                            delayThread.setDaemon(true);
                            delayThread.start();
                        });
                    } else {
                        // Bei weniger als 75% direkt zum Highscore
                        Platform.runLater(() -> finishTraining());
                    }
                }
            }
        };

// Neuer Thread mit einfacher Wartezeit für die Anzeige der Ergebnisse
        Thread warteThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                Platform.runLater(naechsteAktion);
            }
        });

        warteThread.setDaemon(true);
        warteThread.start();
    }

        private void finishTraining() {
            ScoreBoard.ScoreBoardController.setLastSessionList(listId);

        if (stage != null) {
            SceneLoader.load(stage, "/ScoreBoard/ScoreBoard.fxml");

        } else {
            SceneLoader.load("/ScoreBoard/ScoreBoard.fxml");
        }
    }
}