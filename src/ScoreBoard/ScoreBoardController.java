package ScoreBoard;

import Utils.SceneLoader.SceneLoader;
import Utils.StageAwareController;
import Utils.UserSys.UserSys;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
    private  class StatsRow {
        private final String list;
        private final int correct;
        private final int incorrect;

        public StatsRow(String list, int correct, int incorrect) {
            this.list = list;
            this.correct = correct;
            this.incorrect = incorrect;
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
    private TableView<StatsRow> singleTable;
    @FXML
    private TableColumn<StatsRow, String> sListColumn;
    @FXML
    private TableColumn<StatsRow, Integer> sCorrectColumn;
    @FXML
    private TableColumn<StatsRow, Integer> sIncorrectColumn;

    @FXML
    private ChoiceBox<String> listChoiceBox;
    @FXML
    private Label userLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listColumn.setCellValueFactory(new PropertyValueFactory<>("list"));
        correctColumn.setCellValueFactory(new PropertyValueFactory<>("correct"));
        incorrectColumn.setCellValueFactory(new PropertyValueFactory<>("incorrect"));

        sListColumn.setCellValueFactory(new PropertyValueFactory<>("list"));
        sCorrectColumn.setCellValueFactory(new PropertyValueFactory<>("correct"));
        sIncorrectColumn.setCellValueFactory(new PropertyValueFactory<>("incorrect"));

        userLabel.setText("Statistik für " + UserSys.getCurrentUser());
        fillAllTable();
        fillChoiceBox();
        listChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs,o,n) -> updateSingleTable(n));
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
            updateSingleTable(listChoiceBox.getValue());
        }
    }

    /**
     * Update the second table with stats only for the selected list.
     */
    private void updateSingleTable(String listId) {
        if (listId == null) return;
        ObservableList<StatsRow> rows = FXCollections.observableArrayList();
        String user = UserSys.getCurrentUser();
        var stats = UserSys.getUser(user).getStats(listId);
        rows.add(new StatsRow(listId,
                stats.getCorrect(),
                stats.getIncorrect()));
        singleTable.setItems(rows);
    }

    /**
     * Zurück zum Hauptmenü.
     */
    @FXML
    private void backToMenu() {
        SceneLoader.load(stage, "/MainMenu/mainMenu.fxml");
    }
}
