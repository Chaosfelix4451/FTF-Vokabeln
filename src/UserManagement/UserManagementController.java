package UserManagement;

import Utils.SceneLoader.SceneLoader;
import Utils.UserSys.UserSys;
import Utils.StageAwareController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.nio.file.Path;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        UserSys.log("👥 Benutzerverwaltung gestartet");
        UserSys.loadFromJson();
        refreshList("");
        searchField.textProperty().addListener((obs, o, n) -> refreshList(n));
    }

    /**
     * Erstellt einen neuen Benutzer und wählt ihn anschließend aus.
     */
    @FXML
    private void createUser() {
        UserSys.log("➕ Benutzer erstellen");
        String name = newUserField.getText().trim();
        if (!name.isEmpty()) {
            UserSys.createUser(name);
            UserSys.setCurrentUser(name);
            UserSys.saveToJson();
            refreshList(searchField.getText().trim());
            newUserField.clear();
        }
    }

    /**
     * Setzt den aktuell ausgewählten Benutzer.
     */
    @FXML
    private void selectUser() {
        UserSys.log("✅ Benutzer auswählen");
        String selected = userList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            UserSys.setCurrentUser(selected);
            UserSys.saveToJson();
        }
    }

    /**
     * Aktualisiert die Benutzerliste entsprechend dem Suchfilter.
     *
     * @param filter Suchbegriff
     */

    private void refreshList(String filter) {
        UserSys.log("🔄 Aktualisiere Liste mit Filter '" + filter + "'");
        userList.getItems().clear();
        for (String name : UserSys.searchUsers(filter)) {
            userList.getItems().add(name);
        }
    }

    /**
     * Zurück zum Hauptmenü.
     */
    @FXML
    private void backToMenu() {
        UserSys.log("↩️ Zurück zum Hauptmenü");
        SceneLoader.load(stage, "/MainMenu/mainMenu.fxml");
    }
}
