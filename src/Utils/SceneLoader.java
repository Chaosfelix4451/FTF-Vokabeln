package Utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class SceneLoader {

    // 🔒 Interner Stage-Manager
    private static class StageManager {
        private static Stage primaryStage;

        public static void setPrimaryStage(Stage stage) {
            primaryStage = stage;
        }

        public static Stage getPrimaryStage() {
            return primaryStage;
        }
    }

    // 🔓 Öffentliche Methode zum Setzen des globalen Stage
    public static void setPrimaryStage(Stage stage) {
        StageManager.setPrimaryStage(stage);
    }

    // 🔁 Szene laden OHNE manuelles Stage-Weiterreichen
    public static void load(String fxmlPath) {
        Stage stage = StageManager.getPrimaryStage();
        if (stage == null) {
            throw new IllegalStateException("Primary Stage wurde nicht gesetzt. Nutze SceneLoader.setPrimaryStage(stage) im Startpunkt.");
        }
        load(stage, fxmlPath);
    }

    // 🔁 Szene laden MIT explizitem Stage (optional, falls du es manuell brauchst)
    public static void load(Stage stage, String fxmlPath) {
        try {
            URL url = SceneLoader.class.getResource(fxmlPath);
            System.out.println("SceneLoader: Lade " + fxmlPath + " -> " + url);
            if (url == null) {
                throw new IllegalArgumentException("FXML-Datei nicht gefunden: " + fxmlPath);
            }

            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof HasStage) {
                ((HasStage) controller).setStage(stage);
            }

            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 🔗 Schnittstelle für Controller, die Zugriff auf die Stage brauchen
    public interface HasStage {
        void setStage(Stage stage);
    }
}
