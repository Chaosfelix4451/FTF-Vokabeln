package ScoreBoard;

import Utils.SceneLoader.SceneLoader;
import Utils.StageAwareController;
import Utils.UserSys.UserSys;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Displays vocabulary statistics using bar charts.
 */
public class ScoreBoardController extends StageAwareController implements Initializable {
    @FXML
    private BarChart<String, Number> overallChart;
    @FXML
    private BarChart<String, Number> comparisonChart;
    @FXML
    private ChoiceBox<String> listChoiceBox;
    @FXML
    private ChoiceBox<String> modeChoiceBox;
    @FXML
    private TextField countField;
    @FXML
    private Label userLabel;

    private static String lastSessionList;

    /** Called by the trainer when a round finished. */
    public static void setLastSessionList(String listId) {
        lastSessionList = listId;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("ScoreBoard: initialize");
        userLabel.setText("Statistik für " + UserSys.getCurrentUser());
        modeChoiceBox.setItems(FXCollections.observableArrayList("User", "List"));
        modeChoiceBox.setValue("User");
        countField.setText("5");
        fillChoiceBox();
        fillOverallChart();
        updateComparisonChart();

        listChoiceBox.getSelectionModel().selectedItemProperty().addListener((o, oldVal, newVal) -> updateComparisonChart());
        modeChoiceBox.getSelectionModel().selectedItemProperty().addListener((o, oldVal, newVal) -> updateComparisonChart());
    }

    private void fillChoiceBox() {
        String user = UserSys.getCurrentUser();
        listChoiceBox.setItems(FXCollections.observableArrayList(UserSys.getAllListIds(user)));
        if (!listChoiceBox.getItems().isEmpty()) {
            if (lastSessionList != null && listChoiceBox.getItems().contains(lastSessionList)) {
                listChoiceBox.setValue(lastSessionList);
            } else {
                listChoiceBox.getSelectionModel().selectFirst();
            }
        }
    }

    private void fillOverallChart() {
        overallChart.getData().clear();
        XYChart.Series<String, Number> correctSeries = new XYChart.Series<>();
        XYChart.Series<String, Number> incorrectSeries = new XYChart.Series<>();
        String user = UserSys.getCurrentUser();

        if (lastSessionList != null) {
            var stats = UserSys.getUser(user).getStats(lastSessionList);
            correctSeries.getData().add(new XYChart.Data<>(lastSessionList, stats.getCorrect()));
            incorrectSeries.getData().add(new XYChart.Data<>(lastSessionList, stats.getIncorrect()));
        } else {
            for (String list : UserSys.getAllListIds(user)) {
                var stats = UserSys.getUser(user).getStats(list);
                correctSeries.getData().add(new XYChart.Data<>(list, stats.getCorrect()));
                incorrectSeries.getData().add(new XYChart.Data<>(list, stats.getIncorrect()));
            }
        }

        correctSeries.setName("Richtig");
        incorrectSeries.setName("Falsch");
        overallChart.getData().addAll(correctSeries, incorrectSeries);
    }

    @FXML
    private void updateComparisonChart() {
        comparisonChart.getData().clear();
        String listId = listChoiceBox.getValue();
        if (listId == null) return;
        String mode = modeChoiceBox.getValue();
        int count = 5;
        try {
            count = Integer.parseInt(countField.getText());
        } catch (NumberFormatException ignored) {}

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Richtig");

        if ("User".equals(mode)) {
            List<String> users = UserSys.getAllUserNames();
            Collections.sort(users, (a, b) -> {
                int ca = UserSys.getUser(a).getStats(listId).getCorrect();
                int cb = UserSys.getUser(b).getStats(listId).getCorrect();
                return Integer.compare(cb, ca);
            });
            int max = Math.min(count, users.size());
            for (int i = 0; i < max; i++) {
                String name = users.get(i);
                int value = UserSys.getUser(name).getStats(listId).getCorrect();
                series.getData().add(new XYChart.Data<>(name, value));
            }
        } else {
            String user = UserSys.getCurrentUser();
            List<String> lists = (List<String>) UserSys.getAllListIds(user);
            Collections.sort(lists, (a, b) -> {
                int ca = UserSys.getUser(user).getStats(a).getCorrect();
                int cb = UserSys.getUser(user).getStats(b).getCorrect();
                return Integer.compare(cb, ca);
            });
            int max = Math.min(count, lists.size());
            for (int i = 0; i < max; i++) {
                String l = lists.get(i);
                int value = UserSys.getUser(user).getStats(l).getCorrect();
                series.getData().add(new XYChart.Data<>(l, value));
            }
        }
        comparisonChart.getData().add(series);
    }

    @FXML
    private void backToMenu() {
        System.out.println("ScoreBoard: back to menu");
        lastSessionList = null;
        SceneLoader.load(stage, "/MainMenu/mainMenu.fxml");
    }
}
