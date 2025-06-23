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


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Steuert den Ablauf des Trainings und wertet die Antworten aus.
 */
public class TrainerController extends StageAwareController {

    /**
     * Explicit public no-arg constructor required for FXMLLoader.
     */
    public TrainerController() {
        // no-op
    }

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
    private int currentIndex = 0;
    private int questionsPerRound = 5;
    private int sessionPoints = 0;
    private List<String> availableLanguages = new ArrayList<>();

    private static class VocabEntry {
        String solution;
        TextField inputField;
        HBox container;
        String difficulty;
        String questionLang;
        String answerLang;
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

    /**
     * Initialisiert den Trainer und lädt die ersten Vokabeln.
     */
    @FXML
    private void initialize() {
        UserSystem.loadFromFile();
        currentUser = UserSystem.getCurrentUser();
        UserSystem.addUser(currentUser);
        sessionPoints = 0;
        if (pointsLabel != null) {
            pointsLabel.setText("Punkte: 0");
        }

        var prefs = java.util.prefs.Preferences.userNodeForPackage(SettingsController.class);
        mode = prefs.get("vocabMode", "Deutsch zu Englisch");
        listId = prefs.get("vocabFile", "defaultvocab.json");
        model = new TrainerModel();
        String vocabPath = "src/Trainer/Vocabsets/" + listId;
        model.LoadJSONtoDataObj(vocabPath);
        availableLanguages = new ArrayList<>(model.getAvailableLanguages());
        UserSystem.startNewSession(currentUser, listId);

        remainingIds = new ArrayList<>(model.getAllIds());
        Collections.shuffle(remainingIds);

        loadNextVocabSet();

        nextButton.setOnAction(event -> checkAnswers());
        finishButton.setOnAction(event -> finishTraining());

        backButton.setOnAction(event -> SceneLoader.load(stage, "/MainMenu/mainMenu.fxml"));
    }

    /**
     * Baut die nächste Liste an Vokabeln entsprechend des gewählten Modus auf.
     */
    private void loadNextVocabSet() {
        vocabBox.getChildren().clear();
        vocabEntries.clear();

        if (remainingIds.isEmpty()) {
            finishTraining();
            return;
        }

        int questionCount = Math.min(questionsPerRound, remainingIds.size());
        List<String> selectedIds = new ArrayList<>(remainingIds.subList(0, questionCount));
        remainingIds.subList(0, questionCount).clear();


        for (int i = 0; i < selectedIds.size(); i++) {
            String id = selectedIds.get(i);
            String questionLang, answerLang;

            switch (mode) {
                case "Deutsch zu Englisch":
                    questionLang = "de";
                    answerLang = "en";
                    break;
                case "Englisch zu Deutsch":
                    questionLang = "en";
                    answerLang = "de";
                    break;
                case "Französisch zu Deutsch":
                    questionLang = "fr";
                    answerLang = "de";
                    break;
                case "Deutsch zu Französisch":
                    questionLang = "de";
                    answerLang = "fr";
                    break;
                case "Spanisch zu Deutsch":
                    questionLang = "es";
                    answerLang = "de";
                    break;
                case "Deutsch zu Spanisch":
                    questionLang = "de";
                    answerLang = "es";
                    break;
                case "Zufällig":
                default:
                    List<String> langs = new ArrayList<>(availableLanguages);
                    if (langs.size() < 2) {
                        questionLang = answerLang = langs.isEmpty() ? "" : langs.get(0);
                    } else {
                        Collections.shuffle(langs);
                        questionLang = langs.get(0);
                        answerLang = langs.get(1);
                        if (answerLang.equals(questionLang) && langs.size() > 2) {
                            answerLang = langs.get(2);
                        }
                    }
                    break;
            }


            String question = model.get(id, questionLang);
            String solution = model.get(id, answerLang);
            String diff = model.get(id, "difficulty");


            String labelPrefix = ("Zufällig".equals(mode))
                    ? "(" + langName(questionLang) + " -> " + langName(answerLang) + ") "
                    : "";

            Label vocabLabel = new Label((i + 1) + ". " + labelPrefix + question);
            vocabLabel.setMinWidth(150);

            TextField input = new TextField();
            input.setPromptText("Antwort eingeben...");
            input.setMinWidth(200);
            int currentIndexForEnter = i;
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
            entry.solution = solution;
            entry.inputField = input;
            entry.container = entryBox;
            entry.difficulty = diff;
            entry.questionLang = questionLang;
            entry.answerLang = answerLang;

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
        int correctCount = 0;
        for (int idx = 0; idx < vocabEntries.size(); idx++) {
            VocabEntry entry = vocabEntries.get(idx);
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
            if (!isCorrect) {
                allCorrect = false;
            }

            if (isCorrect) {
                correctCount++;
                int pts = pointsForDifficulty(entry.difficulty);
                UserSystem.addPoints(currentUser, pts);
                sessionPoints += pts;
            }
            UserSystem.recordAnswer(currentUser, isCorrect, listId);
        }

        double percent = (vocabEntries.isEmpty()) ? 0 : (correctCount * 100.0 / vocabEntries.size());
        if (percent >= 50.0) {
            soundModel.playSound("src/Utils/Sound/richtig.mp3");
        } else {
            soundModel.playSound("src/Utils/Sound/falsch.mp3");
        }

        UserSystem.saveToFile();
        if (pointsLabel != null) {
            pointsLabel.setText("Punkte: " + sessionPoints);
        }

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

                if (!remainingIds.isEmpty()) {
                    loadNextVocabSet();
                    nextButton.setDisable(false);
                } else {
                    nextButton.setDisable(false);
                    finishTraining();
                }

            });
        });
        delayThread.start();
    }

    /** Finishes the training and shows the score board. */
    private void finishTraining() {
        SceneLoader.load(stage, "/ScoreBoard/ScoreBoard.fxml");
    }
}
