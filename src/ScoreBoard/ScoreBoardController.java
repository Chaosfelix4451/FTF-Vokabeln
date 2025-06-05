package ScoreBoard;

import Utils.SceneLoader.SceneLoader;
import Utils.UserScore.UserSystem;
import Utils.StageAwareController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
    @FXML
    private TableView<UserRow> scoreTable;
    @FXML
    private TableColumn<UserRow, String> nameColumn;
    @FXML
    private TableColumn<UserRow, Integer> pointsColumn;
    @FXML
    private Label progressLabel;



    private final UserSystem userSystem = UserSystem.getInstance();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        pointsColumn.setCellValueFactory(new PropertyValueFactory<>("points"));
        refreshTable();
        updateProgress();
    }

    /**
     * Aktualisiert die Tabelle mit allen Benutzernamen und deren Punkten.
     */
    private void refreshTable() {
        UserSystem.sortByScoreDescending();
        ObservableList<UserRow> data = FXCollections.observableArrayList();
        for (String name : UserSystem.getAllUserNames()) {
            data.add(new UserRow(name, UserSystem.getPoints(name)));
        }
        scoreTable.setItems(data);
    }

    /**
     * Zeigt den Fortschritt des aktuellen Benutzers seit der letzten Runde an.
     */
    private void updateProgress() {
        String user = UserSystem.getCurrentUser();
        int plus = UserSystem.getDiffCorrect(user, null);
        int minus = UserSystem.getDiffIncorrect(user, null);
        progressLabel.setText("Fortschritt seit letzter Runde: +" + plus + " / -" + minus);
    }

    /**
     * Zurück zum Hauptmenü.
     */
    @FXML
    private void backToMenu() {
        SceneLoader.load(stage, "/MainMenu/mainMenu.fxml");
    }
}
