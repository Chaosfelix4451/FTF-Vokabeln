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

    @FXML
    private VBox vocabBox;
    @FXML
    private Button backButton;
    @FXML
    private Button nextButton;

    private final TrainerModel model = new TrainerModel();
    private final SoundModel soundModel = new SoundModel();
    private final List<VocabEntry> vocabEntries = new ArrayList<>();
    private int currentIndex = 0;
    private int questionsPerRound = 5;
    private Stage stage;

    // Speichert ein Vokabelpaar: Lösung + zugehörige Eingabefelder
    private static class VocabEntry {
        String solution;
        List<TextField> fields = new ArrayList<>();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
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

    /**
     * Lädt die nächste Runde von Vokabeln dynamisch in das UI.
     * Generiert Textfelder je Buchstabe und bereitet Event-Handling vor.
     */
    private void loadNextVocabSet() {
        vocabBox.getChildren().clear();
        vocabEntries.clear();

        // Zufällige Anzahl Vokabeln pro Runde (min 3, max 10 oder verbleibende Anzahl)
        questionsPerRound = ThreadLocalRandom.current().nextInt(3, Math.min(10, model.getSize()) + 1);
        int endIndex = Math.min(currentIndex + questionsPerRound, model.getSize());

        for (int i = currentIndex; i < endIndex; i++) {
            final String sourceWord = model.get(i);           // z. B. "apple"
            final String solutionWord = model.getTranslation(i); // z. B. "Apfel"

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

                // Eingabe: Nur ein Buchstabe erlaubt + Fokus auf nächstes Feld
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
                                    entry.fields.get(index + 1).requestFocus();
                                }
                            }
                        });
                    }
                });

                // Rücksprung bei Backspace (wenn leer)
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

    /**
     * Prüft alle eingegebenen Buchstaben gegen die Lösung,
     * markiert diese farbig, deaktiviert die Eingabefelder
     * und lädt bei Erfolg nach kurzer Pause das nächste Set.
     */
    public void checkAnswers() {
        for (VocabEntry entry : vocabEntries) {
            String expected = entry.solution;

            for (int i = 0; i < entry.fields.size(); i++) {
                TextField field = entry.fields.get(i);
                String input = field.getText().trim();
                char expectedChar = expected.charAt(i);

                if (input.equalsIgnoreCase(String.valueOf(expectedChar))) {
                    field.setStyle("-fx-background-color: lightgreen;");
                    for (int j = i + 1; j < entry.fields.size(); j++) {
                        TextField field2 = entry.fields.get(j);
                    }
                    soundModel.playSound("Un");
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
                        } else {
                            nextButton.setDisable(false);
                            nextButton.setText("Ergebnisse anzeigen");
                            nextButton.setOnAction(new javafx.event.EventHandler<javafx.event.ActionEvent>() {
                                @Override
                                public void handle(javafx.event.ActionEvent event) {
                                    SceneLoader.load(stage, "/src/MainMenu/mainMenu.fxml");
                                }
                            });
                        }
                    }
                });
            }
        });
        delayThread.start();
    }
}
