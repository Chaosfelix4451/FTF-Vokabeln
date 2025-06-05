package UserManagement;

import Utils.SceneLoader.SceneLoader;
import Utils.UserScore.UserSystem;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class UserManagementController implements Initializable, SceneLoader.HasStage {
    @FXML
    private TextField newUserField;
    @FXML
    private TextField searchField;
    @FXML
    private ListView<String> userList;
    @FXML
    private Button backButton;

    private Stage stage;
    private final UserSystem userSystem = UserSystem.getInstance();

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userSystem.loadFromFile();
        refreshList("");
        searchField.textProperty().addListener((obs, o, n) -> refreshList(n));
    }

    @FXML
    private void createUser() {
        String name = newUserField.getText().trim();
        if (!name.isEmpty()) {
            userSystem.addUser(name);
            userSystem.setCurrentUser(name);
            userSystem.saveToFile();
            refreshList(searchField.getText().trim());
            newUserField.clear();
        }
    }

    @FXML
    private void selectUser() {
        String selected = userList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            userSystem.setCurrentUser(selected);
            userSystem.saveToFile();
        }
    }

    private void refreshList(String filter) {
        userList.getItems().clear();
        for (String name : userSystem.getAllUserNames()) {
            if (filter == null || filter.isBlank() || name.toLowerCase().contains(filter.toLowerCase())) {
                userList.getItems().add(name);
            }
        }
    }

    @FXML
    private void backToMenu() {
        SceneLoader.load(stage, "/MainMenu/mainMenu.fxml");
    }
}
