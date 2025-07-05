package Utils.SceneLoader;

import Utils.UserSys.UserSys;
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
            // Status speichern
            boolean wasMaximized = stage.isMaximized();
            boolean wasFullScreen = stage.isFullScreen();
            double oldWidth = stage.getWidth();
            double oldHeight = stage.getHeight();
            double oldX = stage.getX();
            double oldY = stage.getY();

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

            String cssPath = fxmlPath.replace(".fxml", ".css");
            URL cssUrl = SceneLoader.class.getResource(cssPath);
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                System.out.println("⚠️ Keine CSS-Datei gefunden für " + cssPath);
            }
            UserSys.loadFromJson();
            if (UserSys.getBooleanPreference("darkMode", false)) {
                URL darkUrl = SceneLoader.class.getResource("/dark.css");
                if (darkUrl != null) {
                    scene.getStylesheets().add(darkUrl.toExternalForm());
                }
            }

            stage.setResizable(true);
            stage.setScene(scene);

            // Status wiederherstellen (Reihenfolge ist wichtig!)
            if (wasFullScreen) {
                stage.setFullScreen(true);
            } else {
                stage.setFullScreen(false);
                if (wasMaximized) {
                    stage.setMaximized(true);
                } else {
                    stage.setMaximized(false);
                    stage.setWidth(oldWidth);
                    stage.setHeight(oldHeight);
                    stage.setX(oldX);
                    stage.setY(oldY);
                }
            }

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface HasStage {
        void setStage(Stage stage);
    }
}