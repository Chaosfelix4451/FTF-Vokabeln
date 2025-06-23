package Utils.SceneLoader;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class SceneLoader {

    private static class StageManager {
        private static Stage primaryStage;

        public static void setPrimaryStage(Stage stage) {
            primaryStage = stage;
        }

        public static Stage getPrimaryStage() {
            return primaryStage;
        }
    }

    public static void setPrimaryStage(Stage stage) {
        StageManager.setPrimaryStage(stage);
    }

    public static void load(String fxmlPath) {
        Stage stage = StageManager.getPrimaryStage();
        if (stage == null) {
            throw new IllegalStateException("Primary Stage wurde nicht gesetzt. Nutze SceneLoader.setPrimaryStage(stage) im Startpunkt.");
        }
        load(stage, fxmlPath);
    }

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
            
            Scene scene = new Scene(root);

            // CSS-Dateipfad berechnen: gleicher Pfad wie FXML, aber mit .css statt .fxml
            String cssPath = fxmlPath.replace(".fxml", ".css");
            URL cssUrl = SceneLoader.class.getResource(cssPath);
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
                System.out.println(" 🆗 CSS-Datei gefunden für " + cssPath);
            } else {
                System.out.println("⚠️ Keine CSS-Datei gefunden für " + cssPath);
            }

            stage.setScene(scene);
            stage.setMaximized(true); // adjust to current display size
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface HasStage {
        void setStage(Stage stage);
    }
}
