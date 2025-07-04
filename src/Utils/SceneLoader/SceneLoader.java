package Utils.SceneLoader;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.BorderPane;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.io.IOException;
import java.net.URL;
import Utils.UserSys.UserSys;

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
            Parent content = loader.load();

            Object controller = loader.getController();
            if (controller instanceof HasStage) {
                ((HasStage) controller).setStage(stage);
            }

            StackPane wrapper = new StackPane(content);
            wrapper.getStyleClass().add("responsive-wrapper");
            Scene scene = new Scene(wrapper);
            applyResponsivePadding(wrapper, stage, fxmlPath);

            // CSS-Dateipfad berechnen: gleicher Pfad wie FXML, aber mit .css statt .fxml
            String cssPath = fxmlPath.replace(".fxml", ".css");
            URL cssUrl = SceneLoader.class.getResource(cssPath);
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
                System.out.println(" 🆗 CSS-Datei gefunden für " + cssPath);
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

            URL responsiveUrl = SceneLoader.class.getResource("/responsive.css");
            if (responsiveUrl != null) {
                scene.getStylesheets().add(responsiveUrl.toExternalForm());
            }

            stage.setScene(scene);
            stage.setFullScreen(!fxmlPath.contains("Settings"));
            stage.setMaximized(true);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void applyResponsivePadding(StackPane wrapper, Stage stage, String fxmlPath) {
        if (fxmlPath.contains("Settings")) {
            return;
        }
        ChangeListener<Number> listener = (obs, o, n) -> {
            double w = stage.getWidth();
            double h = stage.getHeight();
            double ratio = (w < 800 || h < 600) ? 0.05 : 0.1;
            wrapper.setPadding(new Insets(h * ratio, w * ratio, h * ratio, w * ratio));
        };
        stage.widthProperty().addListener(listener);
        stage.heightProperty().addListener(listener);
        // initial call
        listener.changed(null, null, null);
    }

    public interface HasStage {
        void setStage(Stage stage);
    }
}
