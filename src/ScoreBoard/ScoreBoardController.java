package ScoreBoard;

import Utils.SceneLoader.SceneLoader;
import Utils.StageAwareController;
import Utils.UserSys.UserSys;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Zeigt Vokabelstatistiken mithilfe von Balkendiagrammen an.
 */
public class ScoreBoardController extends StageAwareController implements Initializable {
    @FXML
    private Label userLabel;
    @FXML
    private ChoiceBox<String> listChoiceBox;
    @FXML
    private ChoiceBox<String> modeChoiceBox;
    @FXML
    private TextField countField;
    @FXML
    private BarChart<String, Number> overallChart;
    @FXML
    private BarChart<String, Number> comparisonChart;
    @FXML
    private BorderPane rootPane;

    private static String lastSessionList;

    /**
     * Wird vom Trainer aufgerufen, um die zuletzt gespielte Liste zu merken.
     */
    public static void setLastSessionList(String listId) {
        lastSessionList = listId;
    }

    /**
     * Initialisiert die Ansicht und füllt Diagramme sowie Auswahlfelder.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userLabel.setText("Statistik für " + UserSys.getCurrentUser());
        modeChoiceBox.setItems(FXCollections.observableArrayList("User", "List"));
        modeChoiceBox.setValue("User");
        countField.setText("5");

        fillChoiceBox();
        updateOverallChart();
        updateComparisonChart();

        modeChoiceBox.getSelectionModel().selectedItemProperty()
                .addListener((o, ov, nv) -> updateComparisonChart());
        listChoiceBox.getSelectionModel().selectedItemProperty()
                .addListener((o, ov, nv) -> updateComparisonChart());
    }

    /**
     * Füllt die Auswahlliste der verfügbaren Vokabeldateien.
     */
    private void fillChoiceBox() {
        List<String> lists = new java.util.ArrayList<>(UserSys.getAllListIds(UserSys.getCurrentUser()));
        listChoiceBox.setItems(FXCollections.observableArrayList(lists));
        if (lastSessionList != null && lists.contains(lastSessionList)) {
            listChoiceBox.setValue(lastSessionList);
        } else if (!lists.isEmpty()) {
            listChoiceBox.getSelectionModel().selectFirst();
        }
    }

    /**
     * Aktualisiert das Gesamtdiagramm mit allen bisher erreichten Ergebnissen.
     */
    private void updateOverallChart() {
        overallChart.getData().clear();
        XYChart.Series<String, Number> correct = new XYChart.Series<>();
        XYChart.Series<String, Number> wrong = new XYChart.Series<>();
        correct.setName("Richtig");
        wrong.setName("Falsch");

        String user = UserSys.getCurrentUser();
        if (lastSessionList != null) {
            var s = UserSys.getUser(user).getStats(lastSessionList);
            correct.getData().add(new XYChart.Data<>(lastSessionList, s.getCorrect()));
            wrong.getData().add(new XYChart.Data<>(lastSessionList, s.getIncorrect()));
        } else {
            for (String list : UserSys.getAllListIds(user)) {
                var s = UserSys.getUser(user).getStats(list);
                correct.getData().add(new XYChart.Data<>(list, s.getCorrect()));
                wrong.getData().add(new XYChart.Data<>(list, s.getIncorrect()));
            }
        }
        overallChart.getData().addAll(correct, wrong);
    }

    /**
     * Baut das Vergleichsdiagramm abhängig vom gewählten Modus auf.
     */
    @FXML
    private void updateComparisonChart() {
        comparisonChart.getData().clear();
        String listId = listChoiceBox.getValue();
        if (listId == null) {
            return;
        }

        int max;
        try {
            max = Integer.parseInt(countField.getText());
        } catch (NumberFormatException e) {
            max = 5;
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Richtig");

        if ("User".equals(modeChoiceBox.getValue())) {
            List<String> users = UserSys.getAllUserNames().stream()
                    .sorted(Comparator.comparingInt(u -> -UserSys.getUser(u).getStats(listId).getCorrect()))
                    .limit(max)
                    .collect(Collectors.toList());
            for (String u : users) {
                int value = UserSys.getUser(u).getStats(listId).getCorrect();
                series.getData().add(new XYChart.Data<>(u, value));
            }
        } else {
            String user = UserSys.getCurrentUser();
            List<String> lists = UserSys.getAllListIds(user).stream()
                    .sorted(Comparator.comparingInt(l -> -UserSys.getUser(user).getStats(l).getCorrect()))
                    .limit(max)
                    .collect(Collectors.toList());
            for (String l : lists) {
                int value = UserSys.getUser(user).getStats(l).getCorrect();
                series.getData().add(new XYChart.Data<>(l, value));
            }
        }
        comparisonChart.getData().add(series);
    }

    /**
     * Zurück zum Hauptmenü wechseln.
     */
    @FXML
    private void backToMenu() {
        lastSessionList = null;
        SceneLoader.load(stage, "/MainMenu/mainMenu.fxml");
    }

    /**
     * Übergibt die Stage und passt die Größe des Wurzelelements an.
     */
    @Override
    public void setStage(Stage stage) {
        super.setStage(stage);
        if (rootPane != null) {
            rootPane.maxWidthProperty().bind(stage.widthProperty().multiply(0.8));
            rootPane.maxHeightProperty().bind(stage.heightProperty().multiply(0.8));
        }
    }
}
