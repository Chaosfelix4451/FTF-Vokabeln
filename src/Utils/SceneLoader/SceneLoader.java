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
            applyResponsiveSize(wrapper, content, stage, fxmlPath);

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

            boolean isSettings = fxmlPath.contains("Settings");
            stage.setScene(scene);
            stage.setFullScreen(!isSettings);
            stage.setMaximized(!isSettings);
            stage.setResizable(true);
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
            wrapper.setPadding(Insets.EMPTY);
        };
        stage.widthProperty().addListener(listener);
        stage.heightProperty().addListener(listener);
        // initial call
        listener.changed(null, null, null);
    }

    private static void applyResponsiveSize(StackPane wrapper, Parent content, Stage stage, String fxmlPath) {
        if (fxmlPath.contains("Settings")) {
            return;
        }
        ChangeListener<Number> listener = (obs, o, n) -> {
            double w = stage.getWidth();
            double h = stage.getHeight();
            double targetW = w * 0.8;
            double targetH = h * 0.8;
            wrapper.setMinWidth(targetW);
            wrapper.setMinHeight(targetH);
            wrapper.setPrefWidth(targetW);
            wrapper.setPrefHeight(targetH);
            wrapper.setMaxWidth(targetW);
            wrapper.setMaxHeight(targetH);
            double scale = Math.min(targetW / 800.0, targetH / 600.0);
            content.setScaleX(scale);
            content.setScaleY(scale);
        };
        stage.widthProperty().addListener(listener);
        stage.heightProperty().addListener(listener);
        listener.changed(null, null, null);
    }

    public interface HasStage {
        void setStage(Stage stage);
    }
}
