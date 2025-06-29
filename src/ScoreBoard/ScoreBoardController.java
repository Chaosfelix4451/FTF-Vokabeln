package ScoreBoard;

import Utils.SceneLoader.SceneLoader;
import Utils.StageAwareController;
import Utils.UserSys.UserSys;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller für die Highscore-Übersicht.
 */
public class ScoreBoardController extends StageAwareController implements Initializable {
    private static class StatsRow {
        private final String list;
        private final int correct;
        private final int incorrect;

        public StatsRow(String list, int correct, int incorrect) {
            this.list = list;
            this.correct = correct;
            this.incorrect = incorrect;
        }
        public String getList() { return list; }
        public int getCorrect() { return correct; }
        public int getIncorrect() { return incorrect; }
    }
    @FXML
    private TableView<StatsRow> allTable;
    @FXML
    private TableColumn<StatsRow, String> listColumn;
    @FXML
    private TableColumn<StatsRow, Integer> correctColumn;
    @FXML
    private TableColumn<StatsRow, Integer> incorrectColumn;


    @FXML
    private ChoiceBox<String> listChoiceBox;
    @FXML
    private Label userLabel;
    @FXML
    private LineChart<String, Number> progressChart;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;

    private final XYChart.Series<String, Number> correctSeries = new XYChart.Series<>();
    private final XYChart.Series<String, Number> incorrectSeries = new XYChart.Series<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listColumn.setCellValueFactory(new PropertyValueFactory<>("list"));
        correctColumn.setCellValueFactory(new PropertyValueFactory<>("correct"));
        incorrectColumn.setCellValueFactory(new PropertyValueFactory<>("incorrect"));

        userLabel.setText("Statistik für " + UserSys.getCurrentUser());
        fillAllTable();
        fillChoiceBox();
        progressChart.getData().addAll(correctSeries, incorrectSeries);
        updateProgress();
        listChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs,o,n) -> updateProgress());
    }

    /**
     * Fill table with stats of the current user for all available lists.
     */
    private void fillAllTable() {
        ObservableList<StatsRow> rows = FXCollections.observableArrayList();
        String user = UserSys.getCurrentUser();
        for (String list : UserSys.getAllListIds(user)) {
            var stats = UserSys.getUser(user).getStats(list);
            rows.add(new StatsRow(list,
                    stats.getCorrect(),
                    stats.getIncorrect()));
        }
        allTable.setItems(rows);
    }

    /**
     * Populate the choice box with all list ids of the user.
     */
    private void fillChoiceBox() {
        listChoiceBox.getItems().clear();
        String user = UserSys.getCurrentUser();
        listChoiceBox.getItems().addAll(UserSys.getAllListIds(user));
        if (!listChoiceBox.getItems().isEmpty()) {
            listChoiceBox.getSelectionModel().selectFirst();
        }
    }

    /**
     * Display progress either for all lists or for the selected list.
     */
    @FXML
    private void updateProgress() {
        String user = UserSys.getCurrentUser();
        String listId = listChoiceBox.getValue();
        correctSeries.setName("Richtig");
        incorrectSeries.setName("Falsch");
        correctSeries.getData().clear();
        incorrectSeries.getData().clear();

        int lastCorrect = 0, correct = 0;
        int lastIncorrect = 0, incorrect = 0;

        if (listId == null) {
            for (String id : UserSys.getAllListIds(user)) {
                var s = UserSys.getUser(user).getStats(id);
                lastCorrect += s.getLastCorrect();
                correct += s.getCorrect();
                lastIncorrect += s.getLastIncorrect();
                incorrect += s.getIncorrect();
            }
        } else {
            var s = UserSys.getUser(user).getStats(listId);
            lastCorrect = s.getLastCorrect();
            correct = s.getCorrect();
            lastIncorrect = s.getLastIncorrect();
            incorrect = s.getIncorrect();
        }

        correctSeries.getData().add(new XYChart.Data<>("Letzte", lastCorrect));
        correctSeries.getData().add(new XYChart.Data<>("Aktuell", correct));
        incorrectSeries.getData().add(new XYChart.Data<>("Letzte", lastIncorrect));
        incorrectSeries.getData().add(new XYChart.Data<>("Aktuell", incorrect));
    }

    /**
     * Reset selection and show progress for all lists.
     */
    @FXML
    private void showAllProgress() {
        listChoiceBox.getSelectionModel().clearSelection();
        updateProgress();
    }



    /**
     * Zurück zum Hauptmenü.
     */
    @FXML
    private void backToMenu() {
        SceneLoader.load(stage, "/MainMenu/mainMenu.fxml");
    }
}
