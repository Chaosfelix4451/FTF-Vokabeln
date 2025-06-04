package Trainer;

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
import Utils.SceneLoader;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class TrainerController {
    private final SoundModel soundModel = new SoundModel();

    @FXML
    private VBox vocabBox;
    @FXML
    private Button backButton;
    @FXML
    private Button nextButton;

    private final TrainerModel model = new TrainerModel();
    private final List<VocabEntry> vocabEntries = new ArrayList<>();
    private int currentIndex = 0;
    private int questionsPerRound = 5;
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private static class VocabEntry {
        String solution;
        List<TextField> fields = new ArrayList<>();
    }

    @FXML
    private void initialize() {
        loadNextVocabSet();

        nextButton.setOnAction(new javafx.event.EventHandler<javafx.event.ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent event) {
                checkAnswers();
            }
        });

        backButton.setOnAction(new javafx.event.EventHandler<javafx.event.ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent event) {
                SceneLoader.load(stage, "/src/MainMenu/mainMenu.fxml");
            }
        });
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

                // KeyTyped: Nur 1 Zeichen + Weiterleitung
                field.setOnKeyTyped(new javafx.event.EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent event) {
                        String character = event.getCharacter();
                        if (!character.matches("[a-zA-ZäöüÄÖÜß]")) {
                            event.consume();
                            return;
                        }

                        field.setText(character);
                        event.consume();

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                field.positionCaret(1);
                                if (index < solutionWord.length() - 1) {
                                    TextField next = entry.fields.get(index + 1);
                                    next.requestFocus();
                                }
                            }
                        });
                    }
                });

                // Backspace → zum vorherigen Feld
                field.setOnKeyPressed(new javafx.event.EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent event) {
                        if (event.getCode() == KeyCode.BACK_SPACE && field.getText().isEmpty() && index > 0) {
                            final TextField prev = entry.fields.get(index - 1);
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    prev.requestFocus();
                                    prev.clear();
                                }
                            });
                            event.consume();
                        }
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
        for (VocabEntry entry : vocabEntries) {
            String expected = entry.solution;

            for (int i = 0; i < entry.fields.size(); i++) {
                TextField field = entry.fields.get(i);
                String input = field.getText().trim();
                char expectedChar = expected.charAt(i);

                if (input.equalsIgnoreCase(String.valueOf(expectedChar))) {
                    //Test ob Sound funktioniert
                    soundModel.playSound("C:/Users/toby/IdeaProjects/FTF-Vokabeln/src/Ressourcen/sound.mp3");

                    field.setStyle("-fx-background-color: lightgreen;");
                } else {
                    field.setStyle("-fx-background-color: salmon;");
                }

                field.setEditable(false);
            }
        }

        nextButton.setDisable(true);

        Thread delayThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        currentIndex += questionsPerRound;
                        if (currentIndex < model.getSize()) {
                            loadNextVocabSet();
                            nextButton.setDisable(false);
                        }else{
                            nextButton.setDisable(false);

                            nextButton.setText("Ergebnisse anzeigen");
                            nextButton.setOnAction(event -> SceneLoader.load("/MainMenu/mainMenu.fxml"));
                        }
                    }
                });
            }
        });
        delayThread.start();
    }
}
