package UserManagement;

import Utils.SceneLoader.SceneLoader;
import Utils.UserSys.UserSys;
import Utils.StageAwareController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

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
        System.out.println("[" + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "] 👥 Benutzerverwaltung gestartet");
        UserSys.loadFromJson();
        refreshList("");
        searchField.textProperty().addListener((obs, o, n) -> refreshList(n));
    }

    /**
     * Erstellt einen neuen Benutzer und wählt ihn anschließend aus.
     */
    @FXML
    private void createUser() {
        System.out.println("[" + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "] ➕ Benutzer erstellen");
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
        System.out.println("[" + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "] ✅ Benutzer auswählen");
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
        System.out.println("[" + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "] 🔄 Aktualisiere Liste mit Filter '" + filter + "'");
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
        System.out.println("[" + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "] ↩️ Zurück zum Hauptmenü");
        SceneLoader.load(stage, "/MainMenu/mainMenu.fxml");
    }

    /**
     * Zeigt eine kleine Übersicht der Statistiken des ausgewählten Benutzers an.
     */
    @FXML
    private void showStats() {
        String selected = userList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }
        var user = UserSys.getUser(selected);
        if (user == null) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Punkte: ").append(user.getPoints()).append("\n\n");
        for (String listId : user.getListIds()) {
            var stats = user.getStats(listId);
            sb.append(listId).append(": ")
                    .append(stats.getCorrect()).append("/")
                    .append(stats.getTotal()).append("\n");
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Benutzerstatistik");
        alert.setHeaderText("Statistik für " + selected);
        alert.setContentText(sb.toString());
        alert.showAndWait();
    }

    /**
     * Löscht den aktuell ausgewählten Benutzer nach Bestätigung.
     */
    @FXML
    private void deleteUser() {
        String selected = userList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Benutzer '" + selected + "' wirklich löschen?", ButtonType.OK, ButtonType.CANCEL);
        confirm.setHeaderText(null);
        confirm.setTitle("Benutzer löschen");
        var result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            UserSys.deleteUser(selected);
            UserSys.saveToJson();
            refreshList(searchField.getText().trim());
        }
    }

    /**
     * Entfernt alle Benutzer nach einer zusätzlichen Sicherheitsabfrage.
     */
    @FXML
    private void deleteAllUsers() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Wirklich alle Benutzer löschen?", ButtonType.OK, ButtonType.CANCEL);
        confirm.setHeaderText(null);
        confirm.setTitle("Alle Benutzer löschen");
        var result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            UserSys.deleteUser("@all_admin_1234");
            UserSys.saveToJson();
            refreshList("");
        }
    }
}
