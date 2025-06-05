package UserManagement;

import Utils.SceneLoader.SceneLoader;
import Utils.UserScore.UserSystem;
import Utils.StageAwareController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Verwaltung der vorhandenen Benutzer.
 */
public class UserManagementController extends StageAwareController implements Initializable {
    @FXML
    private TextField newUserField;
    @FXML
    private TextField searchField;
    @FXML
    private ListView<String> userList;
    @FXML
    private Button backButton;


=======
    private final UserSystem userSystem = UserSystem.getInstance();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        UserSystem.loadFromFile();
        refreshList("");
        searchField.textProperty().addListener((obs, o, n) -> refreshList(n));
    }

    /**
     * Erstellt einen neuen Benutzer und wählt ihn anschließend aus.
     */
    @FXML
    private void createUser() {
        String name = newUserField.getText().trim();
        if (!name.isEmpty()) {
            UserSystem.addUser(name);
            UserSystem.setCurrentUser(name);
            UserSystem.saveToFile();
            refreshList(searchField.getText().trim());
            newUserField.clear();
        }
    }

    /**
     * Setzt den aktuell ausgewählten Benutzer.
     */
    @FXML
    private void selectUser() {
        String selected = userList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            UserSystem.setCurrentUser(selected);
            UserSystem.saveToFile();
        }
    }

    /**
     * Aktualisiert die Liste der Benutzer unter Berücksichtigung eines Filters.
     */
    private void refreshList(String filter) {
        userList.getItems().clear();
        for (String name : UserSystem.getAllUserNames()) {
            if (filter == null || filter.isBlank() || name.toLowerCase().contains(filter.toLowerCase())) {
                userList.getItems().add(name);
            }
        }
    }

    /**
     * Zurück zum Hauptmenü.
     */
    @FXML
    private void backToMenu() {
        SceneLoader.load(stage, "/MainMenu/mainMenu.fxml");
    }
}
