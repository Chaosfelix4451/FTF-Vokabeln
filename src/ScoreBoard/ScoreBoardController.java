package ScoreBoard;

import Utils.SceneLoader.SceneLoader;
import Utils.UserScore.UserSystem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ScoreBoardController implements Initializable, SceneLoader.HasStage {
    @FXML
    private TableView<UserRow> scoreTable;
    @FXML
    private TableColumn<UserRow, String> nameColumn;
    @FXML
    private TableColumn<UserRow, Integer> pointsColumn;
    @FXML
    private Label progressLabel;

    private Stage stage;
    private final UserSystem userSystem = UserSystem.getInstance();

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        pointsColumn.setCellValueFactory(new PropertyValueFactory<>("points"));
        refreshTable();
        updateProgress();
    }

    private void refreshTable() {
        userSystem.sortByScoreDescending();
        ObservableList<UserRow> data = FXCollections.observableArrayList();
        for (String name : userSystem.getAllUserNames()) {
            data.add(new UserRow(name, userSystem.getPoints(name)));
        }
        scoreTable.setItems(data);
    }

    private void updateProgress() {
        String user = userSystem.getCurrentUser();
        int plus = userSystem.getDiffCorrect(user, null);
        int minus = userSystem.getDiffIncorrect(user, null);
        progressLabel.setText("Fortschritt seit letzter Runde: +" + plus + " / -" + minus);
    }

    @FXML
    private void backToMenu() {
        SceneLoader.load(stage, "/MainMenu/mainMenu.fxml");
    }

    public static class UserRow {
        private final String name;
        private final int points;
        public UserRow(String name, int points) {
            this.name = name;
            this.points = points;
        }
        public String getName() { return name; }
        public int getPoints() { return points; }
    }
}
